import AuthenticationServices
import Foundation
import TraktAuthKit
import TvManiac
import TvManiacKit
import UIKit

class TraktAuthViewModel: NSObject, ObservableObject {
    private let oauthClient: TraktOAuthClient
    private let authBridge: ObservableTraktAuth

    @Published var error: ApplicationError?

    init(authBridge: ObservableTraktAuth) {
        self.authBridge = authBridge

        let config = try! ConfigLoader.load()

        let oauthConfig = TraktAuthConfiguration.trakt(
            clientId: config.clientId,
            clientSecret: config.clientSecret,
            redirectURL: try! config.getCallbackURL()
        )

        oauthClient = TraktOAuthClient(configuration: oauthConfig)

        super.init()
    }

    @MainActor
    func initiateAuthorization() {
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

                authBridge.saveTokens(
                    accessToken: credential.accessToken,
                    refreshToken: credential.refreshToken,
                    expiresAtSeconds: credential.expiresAt.map { Int64($0.timeIntervalSince1970) }
                )

            } catch let error as TraktAuthError {
                let authError = mapToKMPError(error)
                authBridge.handleError(authError)

            } catch {
                authBridge.handleError(AuthError.Unknown())
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
