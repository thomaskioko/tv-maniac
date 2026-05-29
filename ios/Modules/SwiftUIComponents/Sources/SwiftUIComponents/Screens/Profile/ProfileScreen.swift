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

    public init(
        state: State,
        onSettingsClicked: @escaping () -> Void,
        onLoginClicked: @escaping () -> Void
    ) {
        self.state = state
        self.onSettingsClicked = onSettingsClicked
        self.onLoginClicked = onLoginClicked
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
                    GlassButton(icon: "gearshape", action: onSettingsClicked)
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
                        .frame(height: appTheme.spacing.large)

                    statsSection(stats: userProfile.stats)

                    Spacer()
                        .frame(height: appTheme.spacing.xLarge)
                }
                .background(appTheme.colors.background)
                .offset(y: -10)
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
                        .clear,
                        appTheme.colors.background.opacity(0.6),
                        appTheme.colors.background.opacity(0.8),
                        appTheme.colors.background,
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )

            HStack(alignment: .center, spacing: appTheme.spacing.medium) {
                AvatarView(
                    avatarUrl: userProfile.avatarUrl,
                    size: 80,
                    borderColor: appTheme.colors.accent,
                    borderWidth: 3
                )

                VStack(alignment: .leading, spacing: appTheme.spacing.xSmall) {
                    Text(userProfile.fullName ?? userProfile.username)
                        .textStyle(appTheme.typography.titleLarge)
                        .foregroundStyle(.appOnPrimary)

                    Button(action: {}) {
                        Text(state.editButtonLabel)
                            .textStyle(appTheme.typography.labelMedium)
                            .foregroundStyle(.appOnPrimary)
                            .padding(.horizontal, appTheme.spacing.medium)
                            .padding(.vertical, appTheme.spacing.xSmall)
                            .background(Color.clear)
                            .overlay(
                                RoundedRectangle(cornerRadius: appTheme.shapes.medium)
                                    .stroke(.appOnPrimary, lineWidth: 1)
                            )
                    }
                }

                Spacer()
            }
            .padding(appTheme.spacing.medium)
        }
        .clipped()
    }

    // MARK: - Stats Section

    private func statsSection(stats: SwiftProfileStats) -> some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.medium) {
            HStack {
                ChevronTitle(
                    title: state.statsTitle,
                    chevronStyle: ChevronStyle.chevronOnly,
                    action: {}
                )
            }
            .padding(.horizontal, appTheme.spacing.medium)

            VStack(spacing: appTheme.spacing.small) {
                HStack(spacing: appTheme.spacing.small) {
                    StatsCardItem(systemImage: "play.circle", title: state.episodesWatchedLabel) {
                        bigCount(Int(stats.episodesWatched))
                    }
                    .frame(maxWidth: .infinity)

                    StatsCardItem(systemImage: "tv", title: state.showsWatchedLabel) {
                        bigCount(Int(stats.showsWatched))
                    }
                    .frame(maxWidth: .infinity)
                }

                HStack(spacing: appTheme.spacing.small) {
                    StatsCardItem(systemImage: "clock", title: state.watchTimeLabel) {
                        HStack(spacing: appTheme.spacing.medium) {
                            statColumn(label: state.monthsLabel, value: stats.months)
                            statColumn(label: state.daysLabel, value: stats.days)
                            statColumn(label: state.hoursLabel, value: stats.hours)
                        }
                    }
                    .frame(maxWidth: .infinity)

                    StatsCardItem(systemImage: "list.bullet", title: state.listsLabel) {
                        bigCount(Int(stats.listCount))
                    }
                    .frame(maxWidth: .infinity)
                }
            }
            .padding(.horizontal, appTheme.spacing.medium)
        }
    }

    private func bigCount(_ value: Int) -> some View {
        AnimatedCountText(count: value)
            .textStyle(appTheme.typography.headlineMedium)
            .foregroundStyle(.appOnSurface)
    }

    private func statColumn(label: String, value: Int32) -> some View {
        VStack(spacing: appTheme.spacing.xxSmall) {
            Text("\(value)")
                .textStyle(appTheme.typography.titleMedium)
                .foregroundStyle(.appOnSurface)

            Text(label)
                .textStyle(appTheme.typography.bodySmall)
                .foregroundStyle(.appOnSurface)
                .lineLimit(1)
                .minimumScaleFactor(0.7)
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
                    ShimmerView().frame(maxWidth: .infinity).frame(height: 120)
                    ShimmerView().frame(maxWidth: .infinity).frame(height: 120)
                }

                HStack(spacing: appTheme.spacing.small) {
                    ShimmerView().frame(maxWidth: .infinity).frame(height: 120)
                    ShimmerView().frame(maxWidth: .infinity).frame(height: 120)
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

                Spacer()
                    .frame(height: appTheme.spacing.xSmall)

                VStack(spacing: appTheme.spacing.medium) {
                    Text(state.footerDescription)
                        .textStyle(appTheme.typography.bodyMedium)
                        .foregroundStyle(.appOnSurface)
                        .lineSpacing(appTheme.spacing.xxSmall)
                        .padding(.horizontal, appTheme.spacing.large)

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
                }

                Spacer()
                    .frame(height: appTheme.spacing.xLarge)
            }
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

private enum DimensionConstants {
    static let imageHeight: CGFloat = 350
}

private struct ProfileScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
