import SwiftUI

public struct EpisodeListView: View {
    @Theme private var theme
    @State private var showingAlert: Bool = false

    private let episodeCount: Int64
    private let watchProgress: Float
    private let expandEpisodeItems: Bool
    private let showSeasonWatchStateDialog: Bool
    private let isSeasonWatched: Bool
    private let items: [SwiftEpisode]
    private let onEpisodeHeaderClicked: () -> Void
    private let onWatchedStateClicked: () -> Void

    public init(
        showingAlert: Bool = false,
        episodeCount: Int64,
        watchProgress: Float,
        expandEpisodeItems: Bool,
        showSeasonWatchStateDialog: Bool,
        isSeasonWatched: Bool,
        items: [SwiftEpisode],
        onEpisodeHeaderClicked: @escaping () -> Void,
        onWatchedStateClicked: @escaping () -> Void
    ) {
        self.showingAlert = showingAlert
        self.episodeCount = episodeCount
        self.watchProgress = watchProgress
        self.expandEpisodeItems = expandEpisodeItems
        self.showSeasonWatchStateDialog = showSeasonWatchStateDialog
        self.isSeasonWatched = isSeasonWatched
        self.items = items
        self.onEpisodeHeaderClicked = onEpisodeHeaderClicked
        self.onWatchedStateClicked = onWatchedStateClicked
    }

    public var body: some View {
        VStack {
            EpisodeCollapsible(
                episodeCount: episodeCount,
                watchProgress: CGFloat(watchProgress),
                isCollapsed: expandEpisodeItems,
                onCollapseClicked: onEpisodeHeaderClicked,
                onWatchedStateClicked: {
                    onWatchedStateClicked()
                    showingAlert = !showSeasonWatchStateDialog
                }
            ) {
                verticalEpisodeListView
            }
            .alert(isPresented: $showingAlert, content: {
                let title = isSeasonWatched ? "Mark as unwatched" : "Mark as watched"
                let messageBody = isSeasonWatched ?
                    "Are you sure you want to mark the entire season as unwatched?" :
                    "Are you sure you want to mark the entire season as watched?"
                return Alert(
                    title: Text(title),
                    message: Text(messageBody),
                    primaryButton: .default(Text("No")) {},
                    secondaryButton: .default(Text("Yes"))
                )
            })
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
                            episodeOverView: item.overview
                        )
                    }
                }
            }
        }
    }
}

#Preview {
    EpisodeListView(
        showingAlert: false,
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

    public init(episodeId: Int64, title: String, overview: String, imageUrl: String?) {
        self.episodeId = episodeId
        self.imageUrl = imageUrl
        self.title = title
        self.overview = overview
    }
}
