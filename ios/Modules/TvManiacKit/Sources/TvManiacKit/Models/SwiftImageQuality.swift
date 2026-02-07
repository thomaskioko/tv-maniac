import Foundation

public enum SwiftImageQuality: String, CaseIterable, Equatable {
    case auto = "AUTO"
    case high = "HIGH"
    case medium = "MEDIUM"
    case low = "LOW"

    public init(fromString: String?) {
        guard let value = fromString,
              let quality = SwiftImageQuality(rawValue: value)
        else {
            self = .auto
            return
        }
        self = quality
    }
}
