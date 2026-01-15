import SwiftUI

public struct EpisodeListView: View {
    @Theme private var theme

    private let title: String
    private let episodeCount: Int64
    private let watchProgress: Float
    private let expandEpisodeItems: Bool
    private let showSeasonWatchStateDialog: Bool
    private let isSeasonWatched: Bool
    private let items: [SwiftEpisode]
    private let dayLabelFormat: (_ count: Int) -> String
    private let onEpisodeHeaderClicked: () -> Void
    private let onWatchedStateClicked: () -> Void
    private let onEpisodeWatchToggle: (SwiftEpisode) -> Void

    public init(
        title: String,
        episodeCount: Int64,
        watchProgress: Float,
        expandEpisodeItems: Bool,
        showSeasonWatchStateDialog: Bool,
        isSeasonWatched: Bool,
        items: [SwiftEpisode],
        dayLabelFormat: @escaping (_ count: Int) -> String = { count in count == 1 ? "day" : "days" },
        onEpisodeHeaderClicked: @escaping () -> Void,
        onWatchedStateClicked: @escaping () -> Void,
        onEpisodeWatchToggle: @escaping (SwiftEpisode) -> Void = { _ in }
    ) {
        self.title = title
        self.episodeCount = episodeCount
        self.watchProgress = watchProgress
        self.expandEpisodeItems = expandEpisodeItems
        self.showSeasonWatchStateDialog = showSeasonWatchStateDialog
        self.isSeasonWatched = isSeasonWatched
        self.items = items
        self.dayLabelFormat = dayLabelFormat
        self.onEpisodeHeaderClicked = onEpisodeHeaderClicked
        self.onWatchedStateClicked = onWatchedStateClicked
        self.onEpisodeWatchToggle = onEpisodeWatchToggle
    }

    public var body: some View {
        VStack {
            EpisodeCollapsible(
                title: title,
                episodeCount: episodeCount,
                watchProgress: CGFloat(watchProgress),
                isCollapsed: expandEpisodeItems,
                isSeasonWatched: isSeasonWatched,
                onCollapseClicked: onEpisodeHeaderClicked,
                onWatchedStateClicked: onWatchedStateClicked
            ) {
                verticalEpisodeListView
            }
        }
        .padding(.top, theme.spacing.medium)
    }

    @ViewBuilder
    private var verticalEpisodeListView: some View {
        VStack {
            ScrollView(.vertical, showsIndicators: false) {
                LazyVStack(spacing: theme.spacing.xSmall) {
                    ForEach(items, id: \.episodeId) { item in
                        EpisodeItemView(
                            imageUrl: item.imageUrl,
                            episodeTitle: item.title,
                            episodeOverView: item.overview,
                            isWatched: item.isWatched,
                            isEpisodeUpdating: item.isEpisodeUpdating,
                            daysUntilAir: item.daysUntilAir,
                            hasAired: item.hasAired,
                            dayLabelFormat: dayLabelFormat,
                            onWatchedToggle: { onEpisodeWatchToggle(item) }
                        )
                    }
                }
            }
        }
    }
}

#Preview {
    EpisodeListView(
        title: "Episodes",
        episodeCount: 3,
        watchProgress: 0.4,
        expandEpisodeItems: false,
        showSeasonWatchStateDialog: false,
        isSeasonWatched: false,
        items: [
            .init(
                episodeId: 123,
                title: "E1 Model 101",
                overview: "In 1997, a haunted scientist brushes his family aside for an all-consuming project. In 2022, a renegade fighter battles a powerful robot for vital data.",
                imageUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/8rjILRAlcvI9y7vJuH9yNjKYhta.jpg"
            ),
            .init(
                episodeId: 1234,
                title: "E2 Model 102",
                overview: "Eiko and the Terminator arrive in 1997 with identical missions: find Dr. Malcolm Lee. Meanwhile, Lee's three children sneak out of their apartment.",
                imageUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/xfy7Z5IL5QMJo9XCx69s3HlP8Sl.jpg"
            ),
            .init(
                episodeId: 1233,
                title: "E3 Model 103",
                overview: "Malcolm confides in Kokoro about his recurring nightmare. The three children continue their underground trek, unaware of looming danger.",
                imageUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/uNXoR4PR4Uh2ymXz12Z1mhwZoJS.jpg"
            ),
        ],
        onEpisodeHeaderClicked: {},
        onWatchedStateClicked: {}
    )
}

public struct SwiftEpisode: Identifiable {
    public let id: UUID = .init()
    public let episodeId: Int64
    public let title: String
    public let overview: String
    public let imageUrl: String?
    public let seasonNumber: Int64
    public let episodeNumber: Int64
    public let isWatched: Bool
    public let isEpisodeUpdating: Bool
    public let daysUntilAir: Int64?
    public let hasPreviousUnwatched: Bool
    public let hasAired: Bool

    public init(
        episodeId: Int64,
        title: String,
        overview: String,
        imageUrl: String?,
        seasonNumber: Int64 = 0,
        episodeNumber: Int64 = 0,
        isWatched: Bool = false,
        isEpisodeUpdating: Bool = false,
        daysUntilAir: Int64? = nil,
        hasPreviousUnwatched: Bool = false,
        hasAired: Bool = true
    ) {
        self.episodeId = episodeId
        self.imageUrl = imageUrl
        self.title = title
        self.overview = overview
        self.seasonNumber = seasonNumber
        self.episodeNumber = episodeNumber
        self.isWatched = isWatched
        self.isEpisodeUpdating = isEpisodeUpdating
        self.daysUntilAir = daysUntilAir
        self.hasPreviousUnwatched = hasPreviousUnwatched
        self.hasAired = hasAired
    }
}
