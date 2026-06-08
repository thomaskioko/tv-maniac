import Foundation
import TraktAuthKit

public enum AuthCoordinatorFactory {
    public static func create(
        authRepository: AccountAuthRepository,
        config: AuthClientConfig,
        logger: Logger
    ) -> OAuthCoordinator {
        let fallback = URL(string: "about:blank")!
        let authorizationEndpoint = URL(string: config.authorizationEndpoint) ?? fallback
        let tokenEndpoint = URL(string: config.tokenEndpoint) ?? fallback
        let redirectURL = URL(string: config.redirectUri) ?? fallback

        if authorizationEndpoint == fallback || tokenEndpoint == fallback || redirectURL == fallback {
            logger.error(
                tag: "OAuthCoordinator",
                message: "Invalid OAuth endpoint or redirect URI for provider \(config.provider.name)"
            )
        }

        let configuration = OAuthConfiguration(
            authorizationEndpoint: authorizationEndpoint,
            tokenEndpoint: tokenEndpoint,
            redirectURL: redirectURL,
            clientId: config.clientId,
            clientSecret: config.clientSecret,
            scopes: config.scopes
        )

        return OAuthCoordinator(
            authRepository: authRepository,
            logger: logger,
            configuration: configuration
        )
    }
}
