import Foundation

public enum AuthCoordinatorFactory {
    public static func create(
        authRepository: TraktAuthRepository,
        traktConfig: TraktConfig,
        logger: Logger
    ) -> TraktAuthCoordinator {
        let redirectURL = URL(string: traktConfig.redirectUri)
            ?? URL(string: "about:blank")!

        if redirectURL.absoluteString == "about:blank" {
            logger.error(
                tag: "TraktAuthCoordinator",
                message: "Invalid Trakt redirect URI in TraktConfig"
            )
        }

        return TraktAuthCoordinator(
            authRepository: authRepository,
            logger: logger,
            clientId: traktConfig.clientId,
            clientSecret: traktConfig.clientSecret,
            redirectURL: redirectURL
        )
    }
}
