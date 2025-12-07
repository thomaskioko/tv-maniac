import Foundation

public enum AuthCoordinatorFactory {
    public static func create(
        authRepository: TraktAuthRepository,
        logger: Logger
    ) -> TraktAuthCoordinator {
        let redirectURL = URL(string: BuildConfig.shared.TRAKT_REDIRECT_URI)
            ?? URL(string: "about:blank")!

        if redirectURL.absoluteString == "about:blank" {
            logger.error(
                tag: "TraktAuthCoordinator",
                message: "Invalid Trakt redirect URI in BuildConfig"
            )
        }

        return TraktAuthCoordinator(
            authRepository: authRepository,
            logger: logger,
            clientId: BuildConfig.shared.TRAKT_CLIENT_ID,
            clientSecret: BuildConfig.shared.TRAKT_CLIENT_SECRET,
            redirectURL: redirectURL
        )
    }
}
