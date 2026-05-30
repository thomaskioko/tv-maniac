import DesignSystem
import SwiftUI

public struct ProfileScreen: View {
    public struct State: Equatable {
        public let title: String
        public let isLoading: Bool
        public let userProfile: SwiftProfileInfo?
        public let editButtonLabel: String
        public let statsTitle: String
        public let watchTimeLabel: String
        public let monthsLabel: String
        public let daysLabel: String
        public let hoursLabel: String
        public let episodesWatchedLabel: String
        public let showsWatchedLabel: String
        public let listsLabel: String
        public let listsViewLabel: String
        public let userListsTitle: String
        public let viewAllLabel: String
        public let retryLabel: String
        public let userLists: SwiftSectionState<SwiftProfileList>
        public let unauthenticatedTitle: String
        public let footerDescription: String
        public let signInLabel: String
        public let featureItems: [SwiftFeatureItem]

        public init(
            title: String,
            isLoading: Bool,
            userProfile: SwiftProfileInfo?,
            editButtonLabel: String,
            statsTitle: String,
            watchTimeLabel: String,
            monthsLabel: String,
            daysLabel: String,
            hoursLabel: String,
            episodesWatchedLabel: String,
            showsWatchedLabel: String,
            listsLabel: String,
            listsViewLabel: String,
            userListsTitle: String = "",
            viewAllLabel: String = "",
            retryLabel: String = "",
            userLists: SwiftSectionState<SwiftProfileList> = .empty,
            unauthenticatedTitle: String,
            footerDescription: String,
            signInLabel: String,
            featureItems: [SwiftFeatureItem]
        ) {
            self.title = title
            self.isLoading = isLoading
            self.userProfile = userProfile
            self.editButtonLabel = editButtonLabel
            self.statsTitle = statsTitle
            self.watchTimeLabel = watchTimeLabel
            self.monthsLabel = monthsLabel
            self.daysLabel = daysLabel
            self.hoursLabel = hoursLabel
            self.episodesWatchedLabel = episodesWatchedLabel
            self.showsWatchedLabel = showsWatchedLabel
            self.listsLabel = listsLabel
            self.listsViewLabel = listsViewLabel
            self.userListsTitle = userListsTitle
            self.viewAllLabel = viewAllLabel
            self.retryLabel = retryLabel
            self.userLists = userLists
            self.unauthenticatedTitle = unauthenticatedTitle
            self.footerDescription = footerDescription
            self.signInLabel = signInLabel
            self.featureItems = featureItems
        }
    }

    @Environment(\.appTheme) private var appTheme

    private let state: State
    private let onSettingsClicked: () -> Void
    private let onLoginClicked: () -> Void
    private let onViewListsClicked: () -> Void
    private let onRetryLists: () -> Void

    public init(
        state: State,
        onSettingsClicked: @escaping () -> Void,
        onLoginClicked: @escaping () -> Void,
        onViewListsClicked: @escaping () -> Void = {},
        onRetryLists: @escaping () -> Void = {}
    ) {
        self.state = state
        self.onSettingsClicked = onSettingsClicked
        self.onLoginClicked = onLoginClicked
        self.onViewListsClicked = onViewListsClicked
        self.onRetryLists = onRetryLists
    }

    @SwiftUI.State private var showGlass: Double = 0

    public var body: some View {
        ZStack(alignment: .top) {
            if state.isLoading {
                profileSkeleton
            } else if let userProfile = state.userProfile {
                profileScrollView(userProfile: userProfile)
            } else {
                unauthenticatedScrollView
            }

            LinearGradient(
                colors: [
                    appTheme.colors.background.opacity(0.6),
                    appTheme.colors.background.opacity(0.3),
                    .clear,
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 150)
            .allowsHitTesting(false)

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
                    let headerHeight = DimensionConstants.imageHeight + max(scrollY, 0)

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
                .frame(height: DimensionConstants.imageHeight)

                VStack(spacing: 0) {
                    Spacer()
                        .frame(height: appTheme.spacing.medium)

                    statsSection(stats: userProfile.stats)

                    userListsSection

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
                    gradient: Gradient(colors: [
                        .clear,
                        .clear,
                        .clear,
                        appTheme.colors.scrim.opacity(0.3),
                        appTheme.colors.scrim.opacity(0.6),
                        appTheme.colors.scrim.opacity(0.85),
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
                        .textStyle(appTheme.typography.bodyLargeEmphasized)
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
        CollapsibleSection(title: state.statsTitle) {
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

    // MARK: - User Lists Section

    @ViewBuilder
    private var userListsSection: some View {
        if case .empty = state.userLists {
            EmptyView()
        } else {
            VStack(spacing: 0) {
                Spacer().frame(height: appTheme.spacing.large)

                CollapsibleSection(
                    title: state.userListsTitle,
                    showMore: userListsCount > DimensionConstants.maxInlineLists,
                    onMoreClick: onViewListsClicked
                ) {
                    userListsBody
                }
            }
        }
    }

    @ViewBuilder
    private var userListsBody: some View {
        switch state.userLists {
        case .loading:
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: appTheme.spacing.small) {
                    ForEach(0 ..< 3, id: \.self) { _ in
                        ShimmerView(cornerRadius: appTheme.shapes.large)
                            .frame(width: 210, height: 140)
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

    private var userListsCount: Int {
        if case let .content(lists) = state.userLists {
            return lists.count
        }
        return 0
    }

    private func bigCount(_ value: Int) -> some View {
        StatValueText(count: value)
    }

    private func bigLabel(_ text: String) -> some View {
        Text(text)
            .textStyle(appTheme.typography.headlineLarge)
            .foregroundStyle(.appOnSurface)
            .shadow(color: .black.opacity(0.3), radius: 4, x: 0, y: 2)
            .lineLimit(1)
            .minimumScaleFactor(0.5)
    }

    private func watchTimeSegment(value: Int32, unit: String) -> some View {
        HStack(alignment: .firstTextBaseline, spacing: 2) {
            StatValueText(count: Int(value))
                .lineLimit(1)
                .minimumScaleFactor(0.5)

            Text(unit)
                .textStyle(appTheme.typography.bodySmall)
                .foregroundStyle(appTheme.colors.onSurfaceVariant)
                .lineLimit(1)
        }
    }

    // MARK: - Loading Skeleton

    private var profileSkeleton: some View {
        VStack(spacing: 0) {
            ShimmerView(cornerRadius: 0)
                .frame(height: DimensionConstants.imageHeight)

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
                        .frame(height: 84)

                    Text(state.unauthenticatedTitle)
                        .textStyle(appTheme.typography.headlineLarge)
                        .foregroundStyle(.appOnSurface)
                        .lineSpacing(appTheme.spacing.xSmall)
                        .padding(.horizontal, appTheme.spacing.large)

                    VStack(alignment: .leading, spacing: appTheme.spacing.large) {
                        ForEach(state.featureItems) { item in
                            featureItemView(iconName: item.iconName, title: item.title, description: item.description)
                        }
                    }
                    .padding(.horizontal, appTheme.spacing.large)

                    Spacer(minLength: appTheme.spacing.large)

                    VStack(spacing: appTheme.spacing.medium) {
                        Button(action: onLoginClicked) {
                            Text(state.signInLabel)
                                .textStyle(appTheme.typography.bodyMedium)
                                .foregroundStyle(.appOnButtonBackground)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, appTheme.spacing.medium)
                                .background(.appButtonBackground)
                                .cornerRadius(appTheme.shapes.extraLarge)
                        }
                        .padding(.horizontal, appTheme.spacing.large)

                        Text(state.footerDescription)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundStyle(.appOnSurface)
                            .lineSpacing(appTheme.spacing.xxSmall)
                            .padding(.horizontal, appTheme.spacing.large)
                    }
                    .padding(.bottom, appTheme.spacing.xLarge)
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
                    .foregroundStyle(.appOnSurface)
                    .lineSpacing(2)
            }

            Spacer()
        }
    }
}

private struct StatValueText: View {
    @Environment(\.appTheme) private var appTheme

    let count: Int

    var body: some View {
        AnimatedCountText(count: count)
            .textStyle(appTheme.typography.headlineLargeEmphasized)
            .foregroundStyle(.appOnSurface)
            .shadow(color: .black.opacity(0.3), radius: 4, x: 0, y: 2)
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 310
    static let maxInlineLists: Int = 4
}

private struct ProfileScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
