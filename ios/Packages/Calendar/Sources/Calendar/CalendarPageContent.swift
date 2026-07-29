import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

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

    @Environment(\.appTheme) private var theme

    private let state: State
    private let lockedBadgeText: String
    private let lockedActionText: String
    private let lockedAccessibilityLabel: String
    private let onUpgradeClicked: () -> Void
    private let moreEpisodesFormat: (Int32) -> String
    private let onPreviousWeek: () -> Void
    private let onNextWeek: () -> Void
    private let onEpisodeCardClicked: (Int64) -> Void

    public init(
        state: State,
        lockedBadgeText: String = "",
        lockedActionText: String = "",
        lockedAccessibilityLabel: String = "",
        onUpgradeClicked: @escaping () -> Void = {},
        moreEpisodesFormat: @escaping (Int32) -> String,
        onPreviousWeek: @escaping () -> Void,
        onNextWeek: @escaping () -> Void,
        onEpisodeCardClicked: @escaping (Int64) -> Void
    ) {
        self.state = state
        self.lockedBadgeText = lockedBadgeText
        self.lockedActionText = lockedActionText
        self.lockedAccessibilityLabel = lockedAccessibilityLabel
        self.onUpgradeClicked = onUpgradeClicked
        self.moreEpisodesFormat = moreEpisodesFormat
        self.onPreviousWeek = onPreviousWeek
        self.onNextWeek = onNextWeek
        self.onEpisodeCardClicked = onEpisodeCardClicked
    }

    private var isLocked: Bool {
        if case .locked = state.screenState { return true }
        return false
    }

    private var weekNavigationHeader: some View {
        WeekNavigationHeader(
            weekLabel: state.weekLabel,
            canNavigatePrevious: state.canNavigatePrevious,
            canNavigateNext: state.canNavigateNext,
            isRefreshing: state.isRefreshing,
            onPreviousWeek: onPreviousWeek,
            onNextWeek: onNextWeek
        )
    }

    public var body: some View {
        Group {
            if state.useToolbar {
                VStack(spacing: 0) {
                    contentView
                }
                .navigationBarTitleDisplayMode(.inline)
                .toolbar(isLocked ? .hidden : .automatic, for: .navigationBar)
                .toolbar {
                    if !isLocked {
                        ToolbarItem(placement: .principal) {
                            weekNavigationHeader
                        }
                    }
                }
                .toolbarBackground(.appSurface, for: .navigationBar)
                .toolbarBackground(.visible, for: .navigationBar)
            } else {
                VStack(spacing: 0) {
                    if !isLocked {
                        weekNavigationHeader
                            .padding(.horizontal, theme.spacing.small)
                            .padding(.vertical, theme.spacing.xSmall)
                    }

                    contentView
                }
            }
        }
        .appScreen()
        .screenTag(CalendarTestTags.shared.SCREEN_TEST_TAG)
    }

    @ViewBuilder
    private var contentView: some View {
        switch state.screenState {
        case let .locked(underlying, title, message):
            stateBody(for: underlying)
                .premiumOverlay(
                    isLocked: true,
                    badgeText: lockedBadgeText,
                    title: title,
                    message: message,
                    actionText: lockedActionText,
                    onActionClick: onUpgradeClicked,
                    accessibilityLabel: lockedAccessibilityLabel.isEmpty ? nil : lockedAccessibilityLabel
                )
        default:
            stateBody(for: state.screenState)
        }
    }

    @ViewBuilder
    private func stateBody(for screenState: CalendarScreenState) -> some View {
        switch screenState {
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
            .screenTag(CalendarTestTags.shared.LOGGED_OUT_STATE_TEST_TAG)
        case let .empty(title, message):
            CenteredFullScreenView {
                EmptyStateView(
                    systemName: "calendar",
                    title: title,
                    message: message
                )
                .frame(maxWidth: .infinity)
            }
        case .locked:
            EmptyView()
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
                .foregroundStyle(.appOnSurface)
                .padding(.top, isFirst ? theme.spacing.xSmall : 0)
                .padding(.vertical, theme.spacing.xSmall)

            ForEach(dateGroup.episodes) { episode in
                CalendarEpisodeItemView(
                    episode: episode,
                    moreEpisodesFormat: moreEpisodesFormat,
                    onEpisodeCardClicked: onEpisodeCardClicked
                )
            }
        }
    }
}
