import Components
import DesignSystem
import Models
import SwiftUI
import TvManiac

public struct ProfileScreen: View {
    @Environment(\.appTheme) var appTheme

    let state: State
    let onSettingsClicked: () -> Void
    let onProviderSelected: (String) -> Void
    let onViewListsClicked: () -> Void
    let onRetryLists: () -> Void
    let onShowClicked: (Int64) -> Void
    let onRetryProgress: () -> Void
    let onViewStatistics: () -> Void

    public init(
        state: State,
        onSettingsClicked: @escaping () -> Void,
        onProviderSelected: @escaping (String) -> Void,
        onViewListsClicked: @escaping () -> Void = {},
        onRetryLists: @escaping () -> Void = {},
        onShowClicked: @escaping (Int64) -> Void = { _ in },
        onRetryProgress: @escaping () -> Void = {},
        onViewStatistics: @escaping () -> Void = {}
    ) {
        self.state = state
        self.onSettingsClicked = onSettingsClicked
        self.onProviderSelected = onProviderSelected
        self.onViewListsClicked = onViewListsClicked
        self.onRetryLists = onRetryLists
        self.onShowClicked = onShowClicked
        self.onRetryProgress = onRetryProgress
        self.onViewStatistics = onViewStatistics
    }

    @SwiftUI.State private var showGlass: Double = 0

    public var body: some View {
        ZStack(alignment: .top) {
            if state.isLoading {
                profileSkeleton
            } else if !state.isAuthenticated {
                unauthenticatedScrollView
            } else if let userProfile = state.userProfile {
                profileScrollView(userProfile: userProfile)
            } else {
                profileSkeleton
            }

            GlassToolbar(
                title: state.title,
                opacity: showGlass,
                trailingIcon: {
                    HStack(spacing: appTheme.spacing.small) {
                        if state.isLoading {
                            GlassButton(action: {}) {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: appTheme.colors.accent))
                            }
                            .allowsHitTesting(false)
                        }

                        GlassButton(icon: "gearshape", action: onSettingsClicked)
                            .testTag(ProfileTestTags.shared.SETTINGS_BUTTON_TEST_TAG)
                    }
                }
            )
            .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass)
        }
        .appScreen()
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .edgesIgnoringSafeArea(.top)
    }

    // MARK: - Authenticated Content

    private func profileScrollView(userProfile: SwiftProfileInfo) -> some View {
        ScrollView(showsIndicators: false) {
            VStack(spacing: 0) {
                GeometryReader { proxy in
                    let scrollY = proxy.frame(in: .named("profileScroll")).minY
                    let headerHeight = ProfileDimensions.imageHeight + max(scrollY, 0)

                    headerContent(userProfile: userProfile, height: headerHeight)
                        .frame(width: proxy.size.width, height: headerHeight)
                        .offset(y: -max(scrollY, 0))
                        .onChange(of: scrollY) { _, newValue in
                            DispatchQueue.main.async {
                                showGlass = newValue < 0
                                    ? ParallaxConstants.glassOpacity(from: newValue)
                                    : 0
                            }
                        }
                }
                .frame(height: ProfileDimensions.imageHeight)

                VStack(spacing: 0) {
                    Spacer()
                        .frame(height: appTheme.spacing.medium)

                    if let stats = userProfile.stats {
                        statsSection(stats: stats)
                    }

                    recentlyWatchedSection

                    progressSection

                    userListsSection

                    favoritesSection

                    Spacer()
                        .frame(height: appTheme.spacing.xLarge)
                }
                .background(appTheme.colors.background)
            }
        }
        .coordinateSpace(name: "profileScroll")
    }

    private func headerContent(userProfile: SwiftProfileInfo, height: CGFloat) -> some View {
        ZStack(alignment: .bottom) {
            HeaderCoverArtWorkView(
                imageUrl: userProfile.backgroundUrl,
                posterHeight: height
            )
            .foregroundStyle(.ultraThinMaterial)
            .overlay(
                LinearGradient(
                    gradient: Gradient(stops: [
                        .init(color: .clear, location: 0.0),
                        .init(color: .clear, location: 0.11),
                        .init(color: .black.opacity(0.8), location: 0.76),
                        .init(color: .black.opacity(0.8), location: 1.0),
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )

            HStack(alignment: .bottom, spacing: appTheme.spacing.medium) {
                AvatarView(
                    avatarUrl: userProfile.avatarUrl,
                    size: 70,
                    borderColor: appTheme.colors.accent,
                    borderWidth: 2
                )

                VStack(alignment: .leading, spacing: appTheme.spacing.xSmall) {
                    Text(userProfile.fullName ?? userProfile.username)
                        .textStyle(appTheme.typography.bodyLarge)
                        .foregroundStyle(.appOnPrimary)

                    Button(action: {}) {
                        Text(state.editButtonLabel)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundStyle(.appOnPrimary)
                            .frame(minWidth: 140, minHeight: 40)
                            .overlay(
                                RoundedRectangle(cornerRadius: appTheme.shapes.medium)
                                    .stroke(.appOnPrimary, lineWidth: 1)
                            )
                    }
                }

                Spacer()
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.bottom, appTheme.spacing.xSmall)
        }
        .clipped()
    }

    // MARK: - Stats Section

    private func statsSection(stats: SwiftProfileStats) -> some View {
        CollapsibleSection(
            title: state.statsTitle,
            showMore: true,
            onMoreClick: onViewStatistics,
            moreTestTag: ProfileTestTags.shared.STATISTICS_ROW_TEST_TAG
        ) {
            VStack(spacing: appTheme.spacing.small) {
                HStack(spacing: appTheme.spacing.small) {
                    StatsCardItem(
                        systemImage: "play.circle.fill",
                        title: state.episodesWatchedLabel
                    ) {
                        bigLabel(stats.episodesWatched)
                    }
                    .frame(maxWidth: .infinity)

                    StatsCardItem(
                        systemImage: "tv.fill",
                        title: state.showsWatchedLabel
                    ) {
                        bigLabel(stats.showsWatched)
                    }
                    .frame(maxWidth: .infinity)
                }

                HStack(spacing: appTheme.spacing.small) {
                    StatsCardItem(
                        systemImage: "clock.fill",
                        title: state.watchTimeLabel
                    ) {
                        HStack(alignment: .firstTextBaseline, spacing: appTheme.spacing.small) {
                            watchTimeSegment(value: stats.months, unit: state.monthsLabel)
                            watchTimeSegment(value: stats.days, unit: state.daysLabel)
                            watchTimeSegment(value: stats.hours, unit: state.hoursLabel)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .frame(maxWidth: .infinity)

                    StatsCardItem(
                        systemImage: "list.bullet",
                        title: state.listsLabel
                    ) {
                        HStack(alignment: .center) {
                            bigCount(Int(stats.listCount))

                            Spacer()

                            Button(action: onViewListsClicked) {
                                Text(state.listsViewLabel)
                                    .textStyle(appTheme.typography.labelMedium)
                                    .foregroundStyle(.appOnSurface)
                                    .padding(.horizontal, appTheme.spacing.small)
                                    .padding(.vertical, appTheme.spacing.xxSmall)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: appTheme.shapes.small)
                                            .stroke(appTheme.colors.onSurfaceVariant.opacity(0.5), lineWidth: 1)
                                    )
                            }
                        }
                        .frame(maxWidth: .infinity)
                    }
                    .frame(maxWidth: .infinity)
                }
            }
            .padding(.horizontal, appTheme.spacing.medium)
        }
    }

    // MARK: - Loading Skeleton

    private var profileSkeleton: some View {
        VStack(spacing: 0) {
            ShimmerView(cornerRadius: 0)
                .frame(height: ProfileDimensions.imageHeight)

            VStack(spacing: appTheme.spacing.small) {
                HStack {
                    ShimmerView()
                        .frame(width: 120, height: 24)
                    Spacer()
                }

                HStack(spacing: appTheme.spacing.small) {
                    ShimmerView(cornerRadius: appTheme.shapes.large).frame(maxWidth: .infinity).frame(height: 138)
                    ShimmerView(cornerRadius: appTheme.shapes.large).frame(maxWidth: .infinity).frame(height: 138)
                }

                HStack(spacing: appTheme.spacing.small) {
                    ShimmerView(cornerRadius: appTheme.shapes.large).frame(maxWidth: .infinity).frame(height: 138)
                    ShimmerView(cornerRadius: appTheme.shapes.large).frame(maxWidth: .infinity).frame(height: 138)
                }
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.top, appTheme.spacing.large)

            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
    }

    // MARK: - Unauthenticated Content

    private var unauthenticatedScrollView: some View {
        GeometryReader { proxy in
            ScrollView(.vertical, showsIndicators: false) {
                VStack(alignment: .leading, spacing: appTheme.spacing.large) {
                    Spacer()
                        .frame(height: appTheme.spacing.xxLarge)

                    Text(state.unauthenticatedTitle)
                        .textStyle(appTheme.typography.displaySmall)
                        .foregroundStyle(.appOnSurface)
                        .lineSpacing(appTheme.spacing.xSmall)
                        .padding(.horizontal, appTheme.spacing.large)

                    VStack(alignment: .leading, spacing: appTheme.spacing.medium) {
                        ForEach(state.featureItems) { item in
                            featureItemView(iconName: item.iconName, title: item.title, description: item.description)
                        }
                    }
                    .padding(.horizontal, appTheme.spacing.large)

                    ProviderSignInCard(
                        title: state.authTitle,
                        description: state.authDescription,
                        providers: state.authProviders,
                        showBackground: false,
                        onProviderSelected: onProviderSelected
                    )
                    .padding(.horizontal, appTheme.spacing.large)
                    .padding(.bottom, appTheme.spacing.medium)
                }
                .frame(minHeight: proxy.size.height, alignment: .top)
                .background(
                    GeometryReader { geometry in
                        Color.clear.preference(
                            key: ProfileScrollOffsetKey.self,
                            value: geometry.frame(in: .named("scrollView")).minY
                        )
                    }
                )
            }
            .coordinateSpace(name: "scrollView")
            .onPreferenceChange(ProfileScrollOffsetKey.self) { offset in
                DispatchQueue.main.async {
                    showGlass = ParallaxConstants.glassOpacity(from: offset)
                }
            }
        }
    }

    private func featureItemView(iconName: String, title: String, description: String) -> some View {
        HStack(alignment: .top, spacing: appTheme.spacing.medium) {
            Image(systemName: iconName)
                .textStyle(appTheme.typography.headlineMedium)
                .foregroundStyle(.appAccent)
                .frame(width: 44, height: 44)

            VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                Text(title)
                    .textStyle(appTheme.typography.titleMedium)
                    .foregroundStyle(.appOnSurface)

                Text(description)
                    .textStyle(appTheme.typography.bodyMedium)
                    .foregroundStyle(.appOnSurfaceVariant)
                    .lineSpacing(appTheme.spacing.xxxSmall)
            }

            Spacer()
        }
    }
}
