//
//  ObservableTraktAuth.swift
//  tv-maniac
//
//  Created by Claude Code
//  Copyright © 2024 orgName. All rights reserved.
//

import Combine
import Foundation
import TvManiac

@MainActor
public class ObservableTraktAuth: ObservableObject {
    private let authRepository: TraktAuthRepository

    @Published public var isAuthenticated: Bool = false
    @Published public var authError: AuthError?

    private var authStateTask: Task<Void, Never>?

    public init(authRepository: TraktAuthRepository) {
        self.authRepository = authRepository

        observeAuthState()
    }

    deinit {
        authStateTask?.cancel()
    }

    private func observeAuthState() {
        authStateTask = Task { [weak self] in
            guard let self else { return }

            for await state in authRepository.state {
                await MainActor.run {
                    self.isAuthenticated = (state == .loggedIn)
                }
            }
        }
    }

    public func saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Int64?
    ) {
        Task {
            try? await authRepository.saveTokens(
                accessToken: accessToken,
                refreshToken: refreshToken,
                expiresAtSeconds: expiresAtSeconds.map { KotlinLong(value: $0) }
            )
        }
    }

    public func handleError(_ error: AuthError) {
        authError = error
        Task {
            try? await authRepository.setAuthError(error: error)
        }
    }

    public func logout() {
        Task {
            try? await authRepository.logout()
        }
    }
}
