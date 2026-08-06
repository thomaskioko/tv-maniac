import Components
import DesignSystem
import SwiftUI
import TvManiac
import TvManiacKit

public struct StatisticsScreen: View {
    @Environment(\.appTheme) private var theme

    private let state: State
    private let backButtonAccessibilityLabel: String
    private let onBack: () -> Void
    private let onUpgradeClicked: () -> Void
    private let onShowClicked: (Int64) -> Void

    public init(
        state: State,
        backButtonAccessibilityLabel: String = "",
        onBack: @escaping () -> Void = {},
        onUpgradeClicked: @escaping () -> Void = {},
        onShowClicked: @escaping (Int64) -> Void = { _ in }
    ) {
        self.state = state
        self.backButtonAccessibilityLabel = backButtonAccessibilityLabel
        self.onBack = onBack
        self.onUpgradeClicked = onUpgradeClicked
        self.onShowClicked = onShowClicked
    }

    public var body: some View {
        stateBody
            .premiumOverlay(
                isLocked: state.isLocked,
                badgeText: state.labels.lockedBadgeText,
                title: state.labels.lockedTitle,
                message: state.labels.lockedMessage,
                actionText: state.labels.lockedActionText,
                onActionClick: onUpgradeClicked,
                accessibilityLabel: state.labels.lockedContentDescription
            )
            .testTag(state.isLocked ? StatisticsTestTags.shared.LOCKED_STATE_TEST_TAG : nil)
            .appScreen()
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarBackButtonHidden(true)
            .navigationBarColor(backgroundColor: .clear)
            .swipeBackGesture(onSwipe: onBack)
            .overlay(
                GlassToolbar(
                    title: state.labels.screenTitle,
                    opacity: 1.0,
                    leadingIcon: {
                        GlassButton(icon: "chevron.left", action: onBack)
                            .accessibilityLabel(backButtonAccessibilityLabel)
                            .testTag(StatisticsTestTags.shared.BACK_BUTTON_TEST_TAG)
                    }
                ),
                alignment: .top
            )
            .edgesIgnoringSafeArea(.top)
    }

    @ViewBuilder
    private var stateBody: some View {
        if state.isLoading {
            LoadingIndicatorView()
                .padding(.top, toolbarInset)
                .testTag(StatisticsTestTags.shared.LOADING_INDICATOR_TEST_TAG)
        } else if state.showEmptyState {
            EmptyStateView(title: state.labels.emptyMessage)
                .padding(.top, toolbarInset)
                .testTag(StatisticsTestTags.shared.EMPTY_STATE_TEST_TAG)
        } else if state.showContent {
            contentScrollView
        }
    }

    private var contentScrollView: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(alignment: .leading, spacing: theme.spacing.large) {
                if let totalWatchTime = state.totalWatchTime {
                    WatchTimeHeroView(
                        watchTime: totalWatchTime,
                        title: state.labels.watchTimeTitle,
                        daysLabel: state.labels.daysLabel,
                        hoursLabel: state.labels.hoursLabel,
                        minutesLabel: state.labels.minutesLabel
                    )
                }

                if !state.tiles.isEmpty {
                    StatisticTileGridView(tiles: state.tiles)
                }

                if state.showsMarkedWatchedTimes {
                    Text(state.labels.markedWatchedNote)
                        .textStyle(theme.typography.bodySmall)
                        .foregroundStyle(theme.colors.onSurfaceVariant)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.horizontal, theme.spacing.medium)
                        .testTag(StatisticsTestTags.shared.MARKED_WATCHED_NOTE_TEST_TAG)
                }

                if !state.mostWatchedShows.isEmpty {
                    MostWatchedShowsSectionView(
                        shows: state.mostWatchedShows,
                        title: state.labels.mostWatchedTitle,
                        onShowClick: onShowClicked
                    )
                }

                if !state.watchStatusBreakdown.isEmpty {
                    WatchStatusSectionView(items: state.watchStatusBreakdown, title: state.labels.watchStatusTitle)
                }

                if !state.ratingBreakdown.isEmpty {
                    RatingsSectionView(ratings: state.ratingBreakdown, title: state.labels.ratingsTitle)
                }
            }
            .padding(.bottom, theme.spacing.large)
        }
        .contentMargins(.top, toolbarInset + theme.spacing.small)
        .testTag(StatisticsTestTags.shared.CONTENT_TEST_TAG)
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}
