import SwiftUI

public enum CalendarScreenState: Equatable {
    case loading
    case loginRequired(title: String, message: String)
    case empty(title: String, message: String)
    case content(dateGroups: [SwiftCalendarDateGroup])
}

public struct CalendarPageContent: View {
    public struct State: Equatable {
        public let screenState: CalendarScreenState
        public let weekLabel: String
        public let canNavigatePrevious: Bool
        public let canNavigateNext: Bool
        public let isRefreshing: Bool
        public let useToolbar: Bool

        public init(
            screenState: CalendarScreenState,
            weekLabel: String,
            canNavigatePrevious: Bool,
            canNavigateNext: Bool,
            isRefreshing: Bool,
            useToolbar: Bool = false
        ) {
            self.screenState = screenState
            self.weekLabel = weekLabel
            self.canNavigatePrevious = canNavigatePrevious
            self.canNavigateNext = canNavigateNext
            self.isRefreshing = isRefreshing
            self.useToolbar = useToolbar
        }
    }

    @Theme private var theme

    private let state: State
    private let moreEpisodesFormat: (Int32) -> String
    private let onPreviousWeek: () -> Void
    private let onNextWeek: () -> Void
    private let onEpisodeCardClicked: (Int64) -> Void

    public init(
        state: State,
        moreEpisodesFormat: @escaping (Int32) -> String,
        onPreviousWeek: @escaping () -> Void,
        onNextWeek: @escaping () -> Void,
        onEpisodeCardClicked: @escaping (Int64) -> Void
    ) {
        self.state = state
        self.moreEpisodesFormat = moreEpisodesFormat
        self.onPreviousWeek = onPreviousWeek
        self.onNextWeek = onNextWeek
        self.onEpisodeCardClicked = onEpisodeCardClicked
    }

    public var body: some View {
        if state.useToolbar {
            VStack(spacing: 0) {
                contentView
            }
            .background(theme.colors.background.ignoresSafeArea())
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    weekNavigationHeader
                }
            }
            .toolbarBackground(theme.colors.surface, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
        } else {
            VStack(spacing: 0) {
                weekNavigationHeader
                    .padding(.horizontal, theme.spacing.small)
                    .padding(.vertical, theme.spacing.xSmall)

                contentView
            }
            .background(theme.colors.background.ignoresSafeArea())
        }
    }

    private var weekNavigationHeader: some View {
        HStack {
            Button(action: onPreviousWeek) {
                Image(systemName: "chevron.left")
                    .foregroundColor(state.canNavigatePrevious ? theme.colors.onSurface : theme.colors.onSurface.opacity(0.3))
            }
            .disabled(!state.canNavigatePrevious)

            Spacer()

            HStack(spacing: theme.spacing.xSmall) {
                Text(state.weekLabel)
                    .textStyle(theme.typography.titleSmall)
                    .foregroundColor(theme.colors.onSurface)
                    .lineLimit(1)

                if state.isRefreshing {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                        .scaleEffect(0.7)
                }
            }

            Spacer()

            Button(action: onNextWeek) {
                Image(systemName: "chevron.right")
                    .foregroundColor(state.canNavigateNext ? theme.colors.onSurface : theme.colors.onSurface.opacity(0.3))
            }
            .disabled(!state.canNavigateNext)
        }
    }

    @ViewBuilder
    private var contentView: some View {
        switch state.screenState {
        case .loading:
            CenteredFullScreenView {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                    .scaleEffect(1.5)
            }
        case let .loginRequired(title, message):
            CenteredFullScreenView {
                EmptyStateView(
                    systemName: "calendar",
                    title: title,
                    message: message
                )
                .frame(maxWidth: .infinity)
            }
        case let .empty(title, message):
            CenteredFullScreenView {
                EmptyStateView(
                    systemName: "calendar",
                    title: title,
                    message: message
                )
                .frame(maxWidth: .infinity)
            }
        case let .content(dateGroups):
            calendarContent(dateGroups: dateGroups)
        }
    }

    private func calendarContent(dateGroups: [SwiftCalendarDateGroup]) -> some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(alignment: .leading, spacing: theme.spacing.medium) {
                ForEach(Array(dateGroups.enumerated()), id: \.element.id) { index, dateGroup in
                    calendarDateSection(dateGroup: dateGroup, isFirst: index == 0)
                }
            }
            .padding(.horizontal)
        }
    }

    private func calendarDateSection(dateGroup: SwiftCalendarDateGroup, isFirst: Bool) -> some View {
        VStack(alignment: .leading, spacing: theme.spacing.small) {
            Text(dateGroup.dateLabel)
                .textStyle(theme.typography.titleMedium)
                .foregroundColor(theme.colors.onSurface)
                .padding(.top, isFirst ? theme.spacing.xSmall : 0)
                .padding(.vertical, theme.spacing.xSmall)

            ForEach(dateGroup.episodes) { episode in
                calendarEpisodeItem(episode: episode)
            }
        }
    }

    private func calendarEpisodeItem(episode: SwiftCalendarEpisodeItem) -> some View {
        Button {
            onEpisodeCardClicked(episode.episodeTraktId)
        } label: {
            VStack(spacing: 0) {
                HStack(spacing: theme.spacing.small) {
                    PosterItemView(
                        title: nil,
                        posterUrl: episode.posterUrl,
                        posterWidth: 90,
                        posterHeight: 120,
                        posterRadius: 0
                    )

                    VStack(alignment: .leading, spacing: 4) {
                        Text(episode.showTitle)
                            .textStyle(theme.typography.titleMedium)
                            .foregroundColor(theme.colors.onSurface)
                            .lineLimit(1)

                        Text(episode.episodeInfo)
                            .textStyle(theme.typography.bodyMedium)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                            .lineLimit(1)

                        if let airTime = episode.airTime {
                            let airTimeText = episode.network.map { "\(airTime) on \($0)" } ?? airTime
                            Text(airTimeText)
                                .textStyle(theme.typography.bodySmall)
                                .foregroundColor(theme.colors.onSurfaceVariant.opacity(0.7))
                        }
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
                    .padding(.vertical, theme.spacing.small)
                    .padding(.trailing, theme.spacing.small)
                }

                if episode.additionalEpisodesCount > 0 {
                    HStack {
                        Text(moreEpisodesFormat(episode.additionalEpisodesCount))
                            .textStyle(theme.typography.labelMedium)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.horizontal, theme.spacing.medium)
                    .padding(.vertical, theme.spacing.small)
                    .background(theme.colors.surfaceVariant)
                }
            }
            .background(theme.colors.surface)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
        }
        .buttonStyle(.plain)
    }
}

public struct CalendarScreen: View {
    public struct State: Equatable {
        public let screenState: CalendarScreenState
        public let weekLabel: String
        public let canNavigatePrevious: Bool
        public let canNavigateNext: Bool
        public let isRefreshing: Bool

        public init(
            screenState: CalendarScreenState,
            weekLabel: String,
            canNavigatePrevious: Bool,
            canNavigateNext: Bool,
            isRefreshing: Bool
        ) {
            self.screenState = screenState
            self.weekLabel = weekLabel
            self.canNavigatePrevious = canNavigatePrevious
            self.canNavigateNext = canNavigateNext
            self.isRefreshing = isRefreshing
        }
    }

    private let state: State
    private let moreEpisodesFormat: (Int32) -> String
    private let onPreviousWeek: () -> Void
    private let onNextWeek: () -> Void
    private let onEpisodeCardClicked: (Int64) -> Void

    public init(
        state: State,
        moreEpisodesFormat: @escaping (Int32) -> String,
        onPreviousWeek: @escaping () -> Void,
        onNextWeek: @escaping () -> Void,
        onEpisodeCardClicked: @escaping (Int64) -> Void
    ) {
        self.state = state
        self.moreEpisodesFormat = moreEpisodesFormat
        self.onPreviousWeek = onPreviousWeek
        self.onNextWeek = onNextWeek
        self.onEpisodeCardClicked = onEpisodeCardClicked
    }

    public var body: some View {
        CalendarPageContent(
            state: CalendarPageContent.State(
                screenState: state.screenState,
                weekLabel: state.weekLabel,
                canNavigatePrevious: state.canNavigatePrevious,
                canNavigateNext: state.canNavigateNext,
                isRefreshing: state.isRefreshing,
                useToolbar: true
            ),
            moreEpisodesFormat: moreEpisodesFormat,
            onPreviousWeek: onPreviousWeek,
            onNextWeek: onNextWeek,
            onEpisodeCardClicked: onEpisodeCardClicked
        )
    }
}

#Preview("Loading") {
    ThemedPreview {
        NavigationStack {
            CalendarScreen(
                state: CalendarScreen.State(
                    screenState: .loading,
                    weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                    canNavigatePrevious: false,
                    canNavigateNext: true,
                    isRefreshing: false
                ),
                moreEpisodesFormat: { "+\($0) episodes" },
                onPreviousWeek: {},
                onNextWeek: {},
                onEpisodeCardClicked: { _ in }
            )
        }
    }
    .preferredColorScheme(.dark)
}

#Preview("Login Required") {
    ThemedPreview {
        NavigationStack {
            CalendarScreen(
                state: CalendarScreen.State(
                    screenState: .loginRequired(
                        title: "Nothing to see here",
                        message: "Login to Trakt to see your calendar"
                    ),
                    weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                    canNavigatePrevious: false,
                    canNavigateNext: false,
                    isRefreshing: false
                ),
                moreEpisodesFormat: { "+\($0) episodes" },
                onPreviousWeek: {},
                onNextWeek: {},
                onEpisodeCardClicked: { _ in }
            )
        }
    }
    .preferredColorScheme(.dark)
}

#Preview("Empty") {
    ThemedPreview {
        NavigationStack {
            CalendarScreen(
                state: CalendarScreen.State(
                    screenState: .empty(
                        title: "Nothing to see here",
                        message: "No upcoming episodes"
                    ),
                    weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                    canNavigatePrevious: false,
                    canNavigateNext: true,
                    isRefreshing: false
                ),
                moreEpisodesFormat: { "+\($0) episodes" },
                onPreviousWeek: {},
                onNextWeek: {},
                onEpisodeCardClicked: { _ in }
            )
        }
    }
    .preferredColorScheme(.dark)
}

#Preview("Content") {
    ThemedPreview {
        NavigationStack {
            CalendarScreen(
                state: CalendarScreen.State(
                    screenState: .content(dateGroups: [
                        SwiftCalendarDateGroup(
                            dateLabel: "Today, Jan 31, 2026",
                            episodes: [
                                SwiftCalendarEpisodeItem(
                                    showTraktId: 1,
                                    episodeTraktId: 100,
                                    showTitle: "Severance",
                                    posterUrl: nil,
                                    episodeInfo: "S02E01 · Hello, Ms. Cobel",
                                    airTime: "03:00",
                                    network: "Apple TV+",
                                    additionalEpisodesCount: 0
                                ),
                            ]
                        ),
                        SwiftCalendarDateGroup(
                            dateLabel: "Tomorrow, Feb 1, 2026",
                            episodes: [
                                SwiftCalendarEpisodeItem(
                                    showTraktId: 2,
                                    episodeTraktId: 200,
                                    showTitle: "Hell's Paradise",
                                    posterUrl: nil,
                                    episodeInfo: "S02E04 · The Battle Begins",
                                    airTime: "15:45",
                                    network: nil,
                                    additionalEpisodesCount: 1
                                ),
                            ]
                        ),
                    ]),
                    weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                    canNavigatePrevious: false,
                    canNavigateNext: true,
                    isRefreshing: false
                ),
                moreEpisodesFormat: { "+\($0) episodes" },
                onPreviousWeek: {},
                onNextWeek: {},
                onEpisodeCardClicked: { _ in }
            )
        }
    }
    .preferredColorScheme(.dark)
}
