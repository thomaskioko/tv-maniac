import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct UpNextTab: View {
    @Theme private var theme
    @State private var toast: Toast?

    private let presenter: UpNextPresenter
    @StateObject @KotlinStateFlow private var uiState: UpNextState

    private var episodesSwift: [SwiftNextEpisode] {
        uiState.episodes.map { $0.toSwift() }
    }

    init(presenter: UpNextPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ZStack {
            theme.colors.background
                .ignoresSafeArea()

            VStack(spacing: 0) {
                sortChipsRow
                contentView
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                titleView
            }
        }
        .toolbarBackground(theme.colors.surface, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .refreshable {
            presenter.dispatch(action: RefreshUpNext())
        }
        .onChange(of: uiState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(
                    type: .error,
                    title: "Error",
                    message: message.message
                )
                presenter.dispatch(action: UpNextMessageShown(id: message.id))
            }
        }
        .toastView(toast: $toast)
    }

    private var sortChipsRow: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: theme.spacing.small) {
                FilterChip(
                    label: String(\.label_upnext_sort_last_watched),
                    isSelected: uiState.sortOption == .lastWatched,
                    onTap: { presenter.dispatch(action: UpNextChangeSortOption(sortOption: .lastWatched)) }
                )
                FilterChip(
                    label: String(\.label_upnext_sort_air_date),
                    isSelected: uiState.sortOption == .airDate,
                    onTap: { presenter.dispatch(action: UpNextChangeSortOption(sortOption: .airDate)) }
                )
            }
            .padding(.horizontal)
            .padding(.vertical, theme.spacing.xSmall)
        }
    }

    @ViewBuilder
    private var contentView: some View {
        if uiState.showLoading {
            Spacer()
        } else if uiState.isEmpty {
            emptyView
        } else {
            listContent
        }
    }

    private var titleView: some View {
        HStack(spacing: theme.spacing.xSmall) {
            Text(String(\.label_discover_up_next))
                .textStyle(theme.typography.titleMedium)
                .lineLimit(1)
                .foregroundColor(theme.colors.onSurface)

            if uiState.isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                    .scaleEffect(0.7)
            }
        }
    }

    @ViewBuilder
    private var listContent: some View {
        ScrollViewReader { proxy in
            ScrollView(showsIndicators: false) {
                LazyVStack(spacing: theme.spacing.small) {
                    ForEach(episodesSwift, id: \.episodeId) { episode in
                        UpNextListItemView(
                            episode: episode,
                            premiereLabel: String(\.badge_premiere),
                            newLabel: String(\.badge_new),
                            onItemClicked: { showTraktId, _ in
                                presenter.dispatch(action: UpNextShowClicked(showTraktId: showTraktId))
                            },
                            onShowTitleClicked: { showTraktId in
                                presenter.dispatch(action: UpNextShowClicked(showTraktId: showTraktId))
                            },
                            onMarkWatched: {
                                presenter.dispatch(action: MarkWatched(
                                    showTraktId: episode.showTraktId,
                                    episodeId: episode.episodeId,
                                    seasonNumber: episode.seasonNumber,
                                    episodeNumber: episode.episodeNumberValue
                                ))
                            }
                        )
                    }
                }
            }
            .onChange(of: episodesSwift.first?.showTraktId) { _, _ in
                withAnimation {
                    if let firstId = episodesSwift.first?.episodeId {
                        proxy.scrollTo(firstId, anchor: .top)
                    }
                }
            }
            .onChange(of: uiState.sortOption) { _, _ in
                withAnimation {
                    if let firstId = episodesSwift.first?.episodeId {
                        proxy.scrollTo(firstId, anchor: .top)
                    }
                }
            }
        }
    }

    @ViewBuilder
    private var emptyView: some View {
        ScrollView {
            CenteredFullScreenView {
                FullScreenView(
                    systemName: "tray",
                    message: String(\.label_upnext_empty),
                    subtitle: nil,
                    color: theme.colors.onSurfaceVariant
                )
                .frame(maxWidth: .infinity)
            }
        }
    }
}
