import Foundation

public struct OAuthConfiguration {
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
}
