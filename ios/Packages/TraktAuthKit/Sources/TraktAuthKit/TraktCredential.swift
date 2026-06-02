import Foundation

public struct TraktCredential {
    public let accessToken: String
    public let refreshToken: String
    public let tokenType: String
    public let expiresAt: Date?

    public init(
        accessToken: String,
        refreshToken: String,
        tokenType: String = "Bearer",
        expiresAt: Date?
    ) {
        self.accessToken = accessToken
        self.refreshToken = refreshToken
        self.tokenType = tokenType
        self.expiresAt = expiresAt
    }
}
