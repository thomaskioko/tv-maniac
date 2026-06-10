import Foundation

public struct SwiftAuthProvider: Identifiable, Equatable {
    public let id: String
    public let label: String
    public let logoName: String

    public init(id: String, label: String, logoName: String) {
        self.id = id
        self.label = label
        self.logoName = logoName
    }
}
