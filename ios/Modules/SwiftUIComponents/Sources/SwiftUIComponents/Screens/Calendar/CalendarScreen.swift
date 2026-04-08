import SwiftUI

public enum CalendarScreenState {
    case loading
    case loginRequired(title: String, message: String)
    case empty(title: String, message: String)
    case content(dateGroups: [SwiftCalendarDateGroup])
}

public struct CalendarPageContent: View {
    @Theme private var theme

    private let state: CalendarScreenState
    private let weekLabel: String
    private let canNavigatePrevious: Bool
    private let canNavigateNext: Bool
    private let isRefreshing: Bool
    private let moreEpisodesFormat: (Int32) -> String
    private let onPreviousWeek: () -> Void
    private let onNextWeek: () -> Void
    private let onEpisodeCardClicked: (Int64) -> Void
    private let useToolbar: Bool

    public init(
        state: CalendarScreenState,
        weekLabel: String,
        canNavigatePrevious: Bool,
        canNavigateNext: Bool,
        isRefreshing: Bool,
        moreEpisodesFormat: @escaping (Int32) -> String,
        onPreviousWeek: @escaping () -> Void,
        onNextWeek: @escaping () -> Void,
        onEpisodeCardClicked: @escaping (Int64) -> Void,
        useToolbar: Bool = false
    ) {
        self.state = state
        self.weekLabel = weekLabel
        self.canNavigatePrevious = canNavigatePrevious
        self.canNavigateNext = canNavigateNext
        self.isRefreshing = isRefreshing
        self.moreEpisodesFormat = moreEpisodesFormat
        self.onPreviousWeek = onPreviousWeek
        self.onNextWeek = onNextWeek
        self.onEpisodeCardClicked = onEpisodeCardClicked
        self.useToolbar = useToolbar
    }

    public var body: some View {
        if useToolbar {
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
                    .foregroundColor(canNavigatePrevious ? theme.colors.onSurface : theme.colors.onSurface.opacity(0.3))
            }
            .disabled(!canNavigatePrevious)

            Spacer()

            HStack(spacing: theme.spacing.xSmall) {
                Text(weekLabel)
                    .textStyle(theme.typography.titleSmall)
                    .foregroundColor(theme.colors.onSurface)
                    .lineLimit(1)

                if isRefreshing {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                        .scaleEffect(0.7)
                }
            }

            Spacer()

            Button(action: onNextWeek) {
                Image(systemName: "chevron.right")
                    .foregroundColor(canNavigateNext ? theme.colors.onSurface : theme.colors.onSurface.opacity(0.3))
            }
            .disabled(!canNavigateNext)
        }
    }

    @ViewBuilder
    private var contentView: some View {
        switch state {
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
    private let state: CalendarScreenState
    private let weekLabel: String
    private let canNavigatePrevious: Bool
    private let canNavigateNext: Bool
    private let isRefreshing: Bool
    private let moreEpisodesFormat: (Int32) -> String
    private let onPreviousWeek: () -> Void
    private let onNextWeek: () -> Void
    private let onEpisodeCardClicked: (Int64) -> Void

    public init(
        state: CalendarScreenState,
        weekLabel: String,
        canNavigatePrevious: Bool,
        canNavigateNext: Bool,
        isRefreshing: Bool,
        moreEpisodesFormat: @escaping (Int32) -> String,
        onPreviousWeek: @escaping () -> Void,
        onNextWeek: @escaping () -> Void,
        onEpisodeCardClicked: @escaping (Int64) -> Void
    ) {
        self.state = state
        self.weekLabel = weekLabel
        self.canNavigatePrevious = canNavigatePrevious
        self.canNavigateNext = canNavigateNext
        self.isRefreshing = isRefreshing
        self.moreEpisodesFormat = moreEpisodesFormat
        self.onPreviousWeek = onPreviousWeek
        self.onNextWeek = onNextWeek
        self.onEpisodeCardClicked = onEpisodeCardClicked
    }

    public var body: some View {
        CalendarPageContent(
            state: state,
            weekLabel: weekLabel,
            canNavigatePrevious: canNavigatePrevious,
            canNavigateNext: canNavigateNext,
            isRefreshing: isRefreshing,
            moreEpisodesFormat: moreEpisodesFormat,
            onPreviousWeek: onPreviousWeek,
            onNextWeek: onNextWeek,
            onEpisodeCardClicked: onEpisodeCardClicked,
            useToolbar: true
        )
    }
}

#Preview("Loading") {
    ThemedPreview {
        NavigationStack {
            CalendarScreen(
                state: .loading,
                weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                canNavigatePrevious: false,
                canNavigateNext: true,
                isRefreshing: false,
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
                state: .loginRequired(
                    title: "Nothing to see here",
                    message: "Login to Trakt to see your calendar"
                ),
                weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                canNavigatePrevious: false,
                canNavigateNext: false,
                isRefreshing: false,
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
                state: .empty(
                    title: "Nothing to see here",
                    message: "No upcoming episodes"
                ),
                weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                canNavigatePrevious: false,
                canNavigateNext: true,
                isRefreshing: false,
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
                state: .content(dateGroups: [
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
                isRefreshing: false,
                moreEpisodesFormat: { "+\($0) episodes" },
                onPreviousWeek: {},
                onNextWeek: {},
                onEpisodeCardClicked: { _ in }
            )
        }
    }
    .preferredColorScheme(.dark)
}
