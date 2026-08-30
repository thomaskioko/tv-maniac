import UIKit

public struct UpNextItem: Identifiable, Equatable {
    public let id: Int64
    public let showName: String
    public let episodeName: String
    public let seasonEpisodeLabel: String
    public let poster: UIImage?
    public let destination: URL?

    public init(
        id: Int64,
        showName: String,
        episodeName: String,
        seasonEpisodeLabel: String,
        poster: UIImage? = nil,
        destination: URL? = nil
    ) {
        self.id = id
        self.showName = showName
        self.episodeName = episodeName
        self.seasonEpisodeLabel = seasonEpisodeLabel
        self.poster = poster
        self.destination = destination
    }
}

public enum UpNextWidgetState: Equatable {
    case placeholder
    case empty(message: String)
    case content(items: [UpNextItem], lastUpdated: String?)
}
