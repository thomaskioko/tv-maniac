import SwiftUI
import SwiftUIComponents
import TvManiac

struct EpisodeListView: View {
    @State private var showingAlert: Bool = false
    private let episodeCount: Int64
    private let watchProgress: Float
    private let expandEpisodeItems: Bool
    private let showSeasonWatchStateDialog: Bool
    private let isSeasonWatched: Bool
    private let items: [EpisodeDetailsModel]
    private let onEpisodeHeaderClicked: () -> Void
    private let onWatchedStateClicked: () -> Void

    init(
        showingAlert: Bool = false,
        episodeCount: Int64,
        watchProgress: Float,
        expandEpisodeItems: Bool,
        showSeasonWatchStateDialog: Bool,
        isSeasonWatched: Bool,
        items: [EpisodeDetailsModel],
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

    var body: some View {
        VStack {
            Collapsible(
                episodeCount: episodeCount,
                watchProgress: CGFloat(watchProgress),
                isCollapsed: expandEpisodeItems,
                onCollapseClicked: onEpisodeHeaderClicked,
                onWatchedStateClicked: {
                    onWatchedStateClicked()
                    showingAlert = !showSeasonWatchStateDialog
                }
            ) {
                VStack {
                    VerticalEpisodeListView(items: items)
                }
            }
            .alert(isPresented: $showingAlert, content: {
                let title = isSeasonWatched ? "Mark as unwatched" : "Mark as watched"
                let messageBody = isSeasonWatched ?
                    "Are you sure you want to mark the entire season as unwatched?" : "Are you sure you want to mark the entire season as watched?"
                return Alert(
                    title: Text(title),
                    message: Text(messageBody),
                    primaryButton: .default(Text("No")) {},
                    secondaryButton: .default(Text("Yes"))
                )
            })
        }
    }

    @ViewBuilder
    func VerticalEpisodeListView(items: [EpisodeDetailsModel]) -> some View {
        VStack {
            ScrollView(.vertical, showsIndicators: false) {
                LazyVStack {
                    ForEach(items, id: \.id) { item in
                        EpisodeItemView(
                            imageUrl: item.imageUrl,
                            episodeTitle: item.episodeNumberTitle,
                            episodeOverView: item.overview
                        )
                        .padding(.top, item.id == items.first?.id ? 16 : 8)
                    }
                }
            }
        }
    }
}
