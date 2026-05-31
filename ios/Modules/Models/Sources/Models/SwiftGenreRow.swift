import Foundation

public struct SwiftGenreRow: Identifiable, Equatable {
    public let id: String
    public let name: String
    public let subtitle: String
    public let shows: [SwiftShow]

    public init(id: String, name: String, subtitle: String, shows: [SwiftShow]) {
        self.id = id
        self.name = name
        self.subtitle = subtitle
        self.shows = shows
    }
}
