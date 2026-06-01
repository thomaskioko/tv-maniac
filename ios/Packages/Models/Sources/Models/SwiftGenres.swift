import Foundation

public struct SwiftGenres: Identifiable, Equatable {
    public var id: String {
        name
    }

    public let name: String

    public init(name: String) {
        self.name = name
    }
}
