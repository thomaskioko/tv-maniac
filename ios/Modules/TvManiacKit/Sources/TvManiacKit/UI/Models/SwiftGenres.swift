import Foundation

public struct SwiftGenres: Identifiable {
    public let id: UUID = UUID()
    public let name: String
    
    public init(name: String) {
        self.name = name
    }
}
