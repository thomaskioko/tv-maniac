import Foundation

// MARK: - SwiftImageQuality

/// Swift representation of image quality settings
public enum SwiftImageQuality: String, CaseIterable, Equatable {
    case high = "HIGH"
    case medium = "MEDIUM"
    case low = "LOW"

    public var displayTitle: String {
        switch self {
        case .high:
            "High Quality"
        case .medium:
            "Medium Quality"
        case .low:
            "Low Quality"
        }
    }

    public var description: String {
        switch self {
        case .high:
            "Best visual experience, uses more data"
        case .medium:
            "Balanced quality and data usage"
        case .low:
            "Saves data, reduced image quality"
        }
    }

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
