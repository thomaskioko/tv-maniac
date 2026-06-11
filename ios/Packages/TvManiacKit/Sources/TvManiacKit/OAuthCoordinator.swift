import AuthenticationServices
import Foundation
import TraktAuthKit
import TvManiac
import UIKit

public class OAuthCoordinator: NSObject, AuthCoordinator {
    private let oauthClient: OAuthClient
    private let authRepository: AccountAuthRepository
    private let logger: Logger

    /// Sentinel for providers whose tokens never expire (e.g. Simkl) and report no expiry; mirrors the Android
    /// NEVER_EXPIRES_SECONDS so the refresh path is never taken. Revocation is handled by the 401 -> logout path.
    private static let neverExpiresSeconds: Int64 = 4_102_444_800

    public init(
        authRepository: AccountAuthRepository,
        logger: Logger,
        configuration: OAuthConfiguration
    ) {
        self.authRepository = authRepository
        self.logger = logger
        oauthClient = OAuthClient(configuration: configuration)
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

                let expiresAtSeconds = credential.expiresAt
                    .map { Int64($0.timeIntervalSince1970) } ?? Self.neverExpiresSeconds

                try await authRepository.saveTokens(
                    accessToken: credential.accessToken,
                    refreshToken: credential.refreshToken,
                    expiresAtSeconds: expiresAtSeconds
                )

            } catch let error as OAuthError {
                logger.error(
                    tag: "OAuthCoordinator",
                    message: "OAuth authorization failed: \(String(describing: error))"
                )
                await handleAuthError(mapToKMPError(error))

            } catch {
                logger.error(
                    tag: "OAuthCoordinator",
                    message: "Authorization or token save failed: \(error.localizedDescription)"
                )
                await handleAuthError(AuthError.Unknown())
            }
        }
    }

    private func handleAuthError(_ error: AuthError) async {
        do {
            try await authRepository.setAuthError(error: error)
        } catch {
            logger.error(
                tag: "OAuthCoordinator",
                message: "Failed to persist auth error state: \(error.localizedDescription)"
            )
        }
    }

    private func mapToKMPError(_ error: OAuthError) -> AuthError {
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
