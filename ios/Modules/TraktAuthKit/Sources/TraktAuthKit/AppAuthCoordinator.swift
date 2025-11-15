import AppAuth
import Foundation

@MainActor
final class AppAuthCoordinator {
    private let configuration: TraktAuthConfiguration
    private var currentAuthorizationFlow: OIDExternalUserAgentSession?

    init(configuration: TraktAuthConfiguration) {
        self.configuration = configuration
    }

    func performAuthorizationFlow(
        presentingViewController: UIViewController
    ) async throws -> TraktCredential {
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

        let authState = try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<OIDAuthState, Error>) in
            currentAuthorizationFlow = OIDAuthState.authState(
                byPresenting: request,
                presenting: presentingViewController
            ) { authState, error in
                if let error {
                    if (error as NSError).code == OIDErrorCode.userCanceledAuthorizationFlow.rawValue {
                        continuation.resume(throwing: TraktAuthError.userCancelled)
                    } else {
                        continuation.resume(throwing: TraktAuthError.authorizationFailed(error))
                    }
                } else if let authState {
                    continuation.resume(returning: authState)
                } else {
                    continuation.resume(throwing: TraktAuthError.invalidTokenResponse)
                }
            }
        }

        guard let accessToken = authState.lastTokenResponse?.accessToken,
              let refreshToken = authState.lastTokenResponse?.refreshToken
        else {
            throw TraktAuthError.invalidTokenResponse
        }

        return TraktCredential(
            accessToken: accessToken,
            refreshToken: refreshToken,
            tokenType: authState.lastTokenResponse?.tokenType ?? "Bearer",
            expiresAt: authState.lastTokenResponse?.accessTokenExpirationDate
        )
    }
}
