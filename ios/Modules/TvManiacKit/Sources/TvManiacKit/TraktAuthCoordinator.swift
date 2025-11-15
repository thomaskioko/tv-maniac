import AuthenticationServices
import Foundation
import TraktAuthKit
import TvManiac
import UIKit

public class TraktAuthCoordinator: NSObject {
    private let oauthClient: TraktOAuthClient
    private let authRepository: TraktAuthRepository

    public init(authRepository: TraktAuthRepository, clientId: String, clientSecret: String, redirectURL: URL) {
        self.authRepository = authRepository

        let oauthConfig = TraktAuthConfiguration.trakt(
            clientId: clientId,
            clientSecret: clientSecret,
            redirectURL: redirectURL
        )

        oauthClient = TraktOAuthClient(configuration: oauthConfig)

        super.init()
    }

    @MainActor
    public func initiateAuthorization() {
        Task { @MainActor in
            guard let windowScene = UIApplication.shared.connectedScenes
                .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
                let window = windowScene.windows.first(where: { $0.isKeyWindow }),
                let rootViewController = window.rootViewController
            else {
                return
            }

            do {
                let credential = try await oauthClient.authorize(
                    presentingViewController: rootViewController
                )

                try? await authRepository.saveTokens(
                    accessToken: credential.accessToken,
                    refreshToken: credential.refreshToken,
                    expiresAtSeconds: credential.expiresAt.map { KotlinLong(value: Int64($0.timeIntervalSince1970)) }
                )

            } catch let error as TraktAuthError {
                let authError = mapToKMPError(error)
                try? await authRepository.setAuthError(error: authError)

            } catch {
                try? await authRepository.setAuthError(error: AuthError.Unknown())
            }
        }
    }

    private func mapToKMPError(_ error: TraktAuthError) -> AuthError {
        switch error {
        case .authorizationFailed, .tokenExchangeFailed, .invalidTokenResponse:
            AuthError.TokenExchangeFailed()
        case .userCancelled:
            AuthError.OAuthCancelled()
        case .configurationInvalid, .unknown:
            AuthError.Unknown()
        }
    }
}
