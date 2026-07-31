import Components
import DesignSystem
import Models
import SwiftUI
import TvManiac

extension ProfileScreen {
    // MARK: - User Lists Section

    @ViewBuilder
    var userListsSection: some View {
        if case .empty = state.userLists {
            EmptyView()
        } else {
            VStack(spacing: 0) {
                Spacer().frame(height: appTheme.spacing.large)

                CollapsibleSection(
                    title: state.userListsTitle,
                    showMore: userListsCount > ProfileDimensions.maxInlineLists,
                    onMoreClick: onViewListsClicked
                ) {
                    userListsBody
                }
            }
        }
    }

    @ViewBuilder
    var userListsBody: some View {
        switch state.userLists {
        case .loading:
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: appTheme.spacing.small) {
                    ForEach(0 ..< 3, id: \.self) { _ in
                        ShimmerView(cornerRadius: appTheme.shapes.large)
                            .frame(width: 200, height: 133)
                    }
                }
                .padding(.horizontal, appTheme.spacing.medium)
            }
        case let .error(message):
            InlineSectionError(
                message: message,
                retryLabel: state.retryLabel,
                onRetry: onRetryLists
            )
        case let .content(lists):
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: appTheme.spacing.small) {
                    ForEach(lists) { list in
                        ListCollageCard(list: list, onClick: {})
                    }
                }
                .padding(.horizontal, appTheme.spacing.medium)
            }
        case .empty:
            EmptyView()
        }
    }

    var userListsCount: Int {
        if case let .content(lists) = state.userLists {
            return lists.count
        }
        return 0
    }

    // MARK: - Progress Section

    @ViewBuilder
    var progressSection: some View {
        if case .empty = state.inProgress, case .empty = state.completed {
            EmptyView()
        } else {
            VStack(spacing: 0) {
                Spacer().frame(height: appTheme.spacing.large)

                ProgressSectionView(
                    inProgress: state.inProgress,
                    completed: state.completed,
                    title: state.progressTitle,
                    inProgressLabel: state.inProgressLabel,
                    completedLabel: state.completedLabel,
                    emptyLabel: state.progressEmptyLabel,
                    retryLabel: state.retryLabel,
                    onShowClick: onShowClicked,
                    onRetry: onRetryProgress
                )
            }
        }
    }

    // MARK: - Recently Watched Section

    @ViewBuilder
    var recentlyWatchedSection: some View {
        if case .empty = state.recentlyWatched {
            EmptyView()
        } else {
            VStack(spacing: 0) {
                Spacer().frame(height: appTheme.spacing.large)

                RecentlyWatchedSectionView(
                    recentlyWatched: state.recentlyWatched,
                    title: state.recentlyWatchedTitle,
                    retryLabel: state.retryLabel,
                    onShowClick: onShowClicked,
                    onRetry: onRetryProgress
                )
            }
        }
    }

    // MARK: - Favorites Section

    @ViewBuilder
    var favoritesSection: some View {
        if case .empty = state.favorites {
            EmptyView()
        } else {
            VStack(spacing: 0) {
                Spacer().frame(height: appTheme.spacing.large)

                FavoritesSectionView(
                    favorites: state.favorites,
                    title: state.favoritesTitle,
                    retryLabel: state.retryLabel,
                    onShowClick: onShowClicked,
                    onRetry: onRetryProgress
                )
            }
        }
    }

    func bigCount(_ value: Int) -> some View {
        StatValueText(count: value)
    }

    func bigLabel(_ text: String) -> some View {
        Text(text)
            .textStyle(appTheme.typography.headlineLarge)
            .foregroundStyle(.appOnSurface)
            .shadow(color: .black.opacity(0.3), radius: 4, x: 0, y: 2)
            .lineLimit(1)
            .minimumScaleFactor(0.5)
    }

    func watchTimeSegment(value: Int32, unit: String) -> some View {
        HStack(alignment: .firstTextBaseline, spacing: appTheme.spacing.xxxSmall) {
            StatValueText(count: Int(value))
                .lineLimit(1)
                .minimumScaleFactor(0.5)

            Text(unit)
                .textStyle(appTheme.typography.bodySmall)
                .foregroundStyle(appTheme.colors.onSurfaceVariant)
                .lineLimit(1)
        }
    }
}
