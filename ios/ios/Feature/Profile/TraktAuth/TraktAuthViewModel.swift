//
//  TraktAuthViewModel.swift
//  tv-maniac
//
//  Created by Kioko on 01/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import OAuthSwift

class TraktAuthViewModel: ObservableObject {
    private let config: Config

    @Published var error: ApplicationError?

    init() {
        do {
            config = try ConfigLoader.load()
        } catch {
            // Fallback config or handle error appropriately
            fatalError("Failed to load configuration: \(error)")
        }
    }

    func initiateAuthorization() {
        Task {
            do {
                let credential = try await performAuthorizationRedirect()

                await MainActor.run {
                    print(credential.oauthToken)
                    print(credential.oauthRefreshToken)
                    // TODO: Save token
                }

            } catch {
                await MainActor.run {
                    let appError = error as? ApplicationError
                    if appError != nil {
                        self.error = appError!
                    }
                }
            }
        }
    }

    private func performAuthorizationRedirect() async throws -> OAuthSwiftCredential {
        let accessTokenUrl = try config.getAccessTokenUrl()
        let authorizeUrl = try config.getAuthorizeUrl()
        let callbackURL = try config.getCallbackURL()

        let oauthswift = OAuth2Swift(
            consumerKey: config.clientId,
            consumerSecret: config.clientSecret,
            authorizeUrl: authorizeUrl,
            accessTokenUrl: accessTokenUrl,
            responseType: config.responseType
        )

        return try await withCheckedThrowingContinuation { continuation in
            oauthswift.authorize(
                withCallbackURL: callbackURL,
                scope: "public",
                state: "RANDOM_STATE"
            ) { result in
                switch result {
                case .success(let (credential, response, _)):
                    print(response as Any)
                    continuation.resume(returning: credential)
                case let .failure(error):
                    print(error, error.localizedDescription, error.errorCode)
                    continuation.resume(throwing: error)
                }
            }
        }
    }
}
