import AuthenticationServices
import Foundation
import TraktAuthKit
import TvManiac
import UIKit

public class TraktAuthCoordinator: NSObject {
    private let oauthClient: TraktOAuthClient
    private let authRepository: TraktAuthRepository
    private let logger: Logger

    public init(
        authRepository: TraktAuthRepository,
        logger: Logger,
        clientId: String,
        clientSecret: String,
        redirectURL: URL
    ) {
        self.authRepository = authRepository
        self.logger = logger

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

                guard let expiresAt = credential.expiresAt else {
                    logger.error(tag: "TraktAuthCoordinator", message: "Token response missing expiration time")
                    await handleAuthError(AuthError.TokenExchangeFailed())
                    return
                }

                try await authRepository.saveTokens(
                    accessToken: credential.accessToken,
                    refreshToken: credential.refreshToken,
                    expiresAtSeconds: Int64(expiresAt.timeIntervalSince1970)
                )

            } catch let error as TraktAuthError {
                logger.error(tag: "TraktAuthCoordinator", message: "OAuth authorization failed: \(String(describing: error))")
                await handleAuthError(mapToKMPError(error))

            } catch {
                logger.error(tag: "TraktAuthCoordinator", message: "Authorization or token save failed: \(error.localizedDescription)")
                await handleAuthError(AuthError.Unknown())
            }
        }
    }

    private func handleAuthError(_ error: AuthError) async {
        do {
            try await authRepository.setAuthError(error: error)
        } catch {
            logger.error(tag: "TraktAuthCoordinator", message: "Failed to persist auth error state: \(error.localizedDescription)")
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
