import Foundation

// MARK: - SwiftImageQuality

/// Swift representation of image quality settings
public enum SwiftImageQuality: String, CaseIterable, Equatable {
    case high = "HIGH"
    case medium = "MEDIUM"
    case low = "LOW"

    /// Initialize from a raw string value with a default fallback
    public init(fromString: String?) {
        guard let value = fromString,
              let quality = SwiftImageQuality(rawValue: value)
        else {
            self = .medium
            return
        }
        self = quality
    }
}
