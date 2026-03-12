import SwiftUI

public struct ProfileScreen: View {
    @Theme private var appTheme

    private let title: String
    private let isLoading: Bool
    private let userProfile: SwiftProfileInfo?
    private let editButtonLabel: String
    private let statsTitle: String
    private let watchTimeLabel: String
    private let monthsLabel: String
    private let daysLabel: String
    private let hoursLabel: String
    private let episodesWatchedLabel: String
    private let unauthenticatedTitle: String
    private let footerDescription: String
    private let signInLabel: String
    private let featureItems: [SwiftFeatureItem]
    private let onSettingsClicked: () -> Void
    private let onLoginClicked: () -> Void

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
        unauthenticatedTitle: String,
        footerDescription: String,
        signInLabel: String,
        featureItems: [SwiftFeatureItem],
        onSettingsClicked: @escaping () -> Void,
        onLoginClicked: @escaping () -> Void
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
        self.unauthenticatedTitle = unauthenticatedTitle
        self.footerDescription = footerDescription
        self.signInLabel = signInLabel
        self.featureItems = featureItems
        self.onSettingsClicked = onSettingsClicked
        self.onLoginClicked = onLoginClicked
    }

    @State private var showGlass: Double = 0

    public var body: some View {
        ZStack(alignment: .top) {
            if isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: appTheme.colors.accent))
                    .scaleEffect(1.5)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if let userProfile {
                profileScrollView(userProfile: userProfile)
            } else {
                unauthenticatedScrollView
            }

            LinearGradient(
                colors: [
                    .black.opacity(0.6),
                    .black.opacity(0.3),
                    .clear,
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 150)
            .allowsHitTesting(false)

            GlassToolbar(
                title: title,
                opacity: showGlass,
                trailingIcon: {
                    GlassButton(icon: "gearshape", action: onSettingsClicked)
                }
            )
            .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass)
        }
        .background(appTheme.colors.background)
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
                        Color.black.opacity(0.6),
                        Color.black.opacity(0.8),
                        Color.black,
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )

            HStack(alignment: .center, spacing: 16) {
                AvatarView(
                    avatarUrl: userProfile.avatarUrl,
                    size: 80,
                    borderColor: appTheme.colors.accent,
                    borderWidth: 3
                )

                VStack(alignment: .leading, spacing: appTheme.spacing.xSmall) {
                    Text(userProfile.fullName ?? userProfile.username)
                        .textStyle(appTheme.typography.titleLarge)
                        .foregroundColor(appTheme.colors.onPrimary)

                    Button(action: {}) {
                        Text(editButtonLabel)
                            .textStyle(appTheme.typography.labelMedium)
                            .foregroundColor(appTheme.colors.onPrimary)
                            .padding(.horizontal, 20)
                            .padding(.vertical, appTheme.spacing.xSmall)
                            .background(Color.clear)
                            .overlay(
                                RoundedRectangle(cornerRadius: appTheme.shapes.medium)
                                    .stroke(appTheme.colors.onPrimary, lineWidth: 1)
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
                    title: statsTitle,
                    chevronStyle: ChevronStyle.chevronOnly,
                    action: {}
                )
            }
            .padding(.horizontal, appTheme.spacing.medium)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .top, spacing: appTheme.spacing.small) {
                    StatsCardItem(
                        systemImage: "calendar",
                        title: watchTimeLabel
                    ) {
                        HStack(spacing: appTheme.spacing.large) {
                            statColumn(label: monthsLabel, value: stats.months)
                            statColumn(label: daysLabel, value: stats.days)
                            statColumn(label: hoursLabel, value: stats.hours)
                        }
                    }

                    StatsCardItem(
                        systemImage: "tv",
                        title: episodesWatchedLabel
                    ) {
                        VStack(spacing: 0) {
                            Text(formatNumber(stats.episodesWatched))
                                .textStyle(appTheme.typography.bodyMedium)
                                .foregroundColor(appTheme.colors.onSurface)
                                .frame(maxWidth: .infinity)
                        }
                        .padding(appTheme.spacing.xSmall)
                    }
                }
                .padding(.horizontal, appTheme.spacing.medium)
            }
        }
    }

    private func statColumn(label: String, value: Int32) -> some View {
        VStack(spacing: appTheme.spacing.xxSmall) {
            Text("\(value)")
                .textStyle(appTheme.typography.titleMedium)
                .foregroundColor(appTheme.colors.onSurface)

            Text(label)
                .textStyle(appTheme.typography.bodySmall)
                .foregroundColor(appTheme.colors.onSurface)
        }
    }

    private func formatNumber(_ number: Int32) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.groupingSeparator = ","
        return formatter.string(from: NSNumber(value: number)) ?? "\(number)"
    }

    // MARK: - Unauthenticated Content

    private var unauthenticatedScrollView: some View {
        ScrollView(.vertical, showsIndicators: false) {
            VStack(alignment: .leading, spacing: appTheme.spacing.large) {
                Spacer()
                    .frame(height: 84)

                Text(unauthenticatedTitle)
                    .textStyle(appTheme.typography.headlineLarge)
                    .foregroundColor(appTheme.colors.onSurface)
                    .lineSpacing(appTheme.spacing.xSmall)
                    .padding(.horizontal, 28)

                VStack(alignment: .leading, spacing: appTheme.spacing.large) {
                    ForEach(featureItems) { item in
                        featureItemView(iconName: item.iconName, title: item.title, description: item.description)
                    }
                }
                .padding(.horizontal, appTheme.spacing.large)

                Spacer()
                    .frame(height: appTheme.spacing.xSmall)

                VStack(spacing: 20) {
                    Text(footerDescription)
                        .textStyle(appTheme.typography.bodyMedium)
                        .foregroundColor(appTheme.colors.onSurface)
                        .lineSpacing(appTheme.spacing.xxSmall)
                        .padding(.horizontal, appTheme.spacing.large)

                    Button(action: onLoginClicked) {
                        Text(signInLabel)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundColor(appTheme.colors.onButtonBackground)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, appTheme.spacing.medium)
                            .background(appTheme.colors.buttonBackground)
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
                .foregroundColor(appTheme.colors.accent)
                .frame(width: 44, height: 44)

            VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                Text(title)
                    .textStyle(appTheme.typography.titleMedium)
                    .foregroundColor(appTheme.colors.onSurface)

                Text(description)
                    .textStyle(appTheme.typography.bodyMedium)
                    .foregroundColor(appTheme.colors.onSurface)
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
