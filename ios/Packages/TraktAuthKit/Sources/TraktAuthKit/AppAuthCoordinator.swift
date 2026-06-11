import AppAuth
import Foundation

@MainActor
final class AppAuthCoordinator {
    private let configuration: OAuthConfiguration
    private var currentAuthorizationFlow: OIDExternalUserAgentSession?

    init(configuration: OAuthConfiguration) {
        self.configuration = configuration
    }

    func performAuthorizationFlow(
        presentingViewController: UIViewController
    ) async throws -> OAuthCredential {
        let serviceConfig = OIDServiceConfiguration(
            authorizationEndpoint: configuration.authorizationEndpoint,
            tokenEndpoint: configuration.tokenEndpoint
        )

        let request = OIDAuthorizationRequest(
            configuration: serviceConfig,
            clientId: configuration.clientId,
            clientSecret: configuration.clientSecret,
            scopes: configuration.scopes,
            redirectURL: configuration.redirectURL,
            responseType: OIDResponseTypeCode,
            additionalParameters: nil
        )

        let authState = try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<
            OIDAuthState,
            Error
        >) in
            currentAuthorizationFlow = OIDAuthState.authState(
                byPresenting: request,
                presenting: presentingViewController
            ) { authState, error in
                if let error {
                    if (error as NSError).code == OIDErrorCode.userCanceledAuthorizationFlow.rawValue {
                        continuation.resume(throwing: OAuthError.userCancelled)
                    } else {
                        continuation.resume(throwing: OAuthError.authorizationFailed(error))
                    }
                } else if let authState {
                    continuation.resume(returning: authState)
                } else {
                    continuation.resume(throwing: OAuthError.invalidTokenResponse)
                }
            }
        }

        guard let accessToken = authState.lastTokenResponse?.accessToken else {
            throw OAuthError.invalidTokenResponse
        }

        return OAuthCredential(
            accessToken: accessToken,
            refreshToken: authState.lastTokenResponse?.refreshToken ?? "",
            tokenType: authState.lastTokenResponse?.tokenType ?? "Bearer",
            expiresAt: authState.lastTokenResponse?.accessTokenExpirationDate
        )
    }
}
