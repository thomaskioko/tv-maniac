import Foundation

public struct TraktAuthConfiguration {
    public let authorizationEndpoint: URL
    public let tokenEndpoint: URL
    public let redirectURL: URL
    public let clientId: String
    public let clientSecret: String
    public let scopes: [String]

    public init(
        authorizationEndpoint: URL,
        tokenEndpoint: URL,
        redirectURL: URL,
        clientId: String,
        clientSecret: String,
        scopes: [String] = []
    ) {
        self.authorizationEndpoint = authorizationEndpoint
        self.tokenEndpoint = tokenEndpoint
        self.redirectURL = redirectURL
        self.clientId = clientId
        self.clientSecret = clientSecret
        self.scopes = scopes
    }

    public static func trakt(
        clientId: String,
        clientSecret: String,
        redirectURL: URL
    ) -> Self {
        Self(
            authorizationEndpoint: URL(string: "https://trakt.tv/oauth/authorize")!,
            tokenEndpoint: URL(string: "https://api.trakt.tv/oauth/token")!,
            redirectURL: redirectURL,
            clientId: clientId,
            clientSecret: clientSecret,
            scopes: []
        )
    }
}
