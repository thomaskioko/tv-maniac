import Foundation

public enum TraktAuthError: Error {
    case authorizationFailed(Error)
    case tokenExchangeFailed(Error)
    case userCancelled
    case invalidTokenResponse
    case configurationInvalid(String)
    case unknown(Error)
}

extension TraktAuthError: LocalizedError {
    public var errorDescription: String? {
        switch self {
        case let .authorizationFailed(error):
            "Authorization failed: \(error.localizedDescription)"
        case let .tokenExchangeFailed(error):
            "Token exchange failed: \(error.localizedDescription)"
        case .userCancelled:
            "User cancelled authorization"
        case .invalidTokenResponse:
            "Invalid token response from server"
        case let .configurationInvalid(reason):
            "Configuration error: \(reason)"
        case let .unknown(error):
            "Unknown error: \(error.localizedDescription)"
        }
    }
}
