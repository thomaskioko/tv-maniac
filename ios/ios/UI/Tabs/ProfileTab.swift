import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ProfileTab: View {
    @Theme private var theme
    @Environment(ToastManager.self) private var toastManager

    private let presenter: ProfilePresenter
    @State private var showGlass: Double = 0
    @StateObject @KotlinStateFlow private var uiState: ProfileState

    init(presenter: ProfilePresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ZStack(alignment: .top) {
            if uiState.showLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                    .scaleEffect(1.5)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if let userProfile = uiState.userProfile {
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
                title: String(\.profile_title),
                opacity: showGlass,
                leadingIcon: {
                    GlassButton(icon: "chevron.left") {
                        presenter.dispatch(action: ProfileActionBackClicked())
                    }
                    .opacity(1 - showGlass)
                },
                trailingIcon: {
                    GlassButton(icon: "gearshape") {
                        presenter.dispatch(action: ProfileActionSettingsClicked())
                    }
                }
            )
            .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass)
        }
        .background(theme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .navigationBarBackButtonHidden(true)
        .swipeBackGesture {
            presenter.dispatch(action: ProfileActionBackClicked())
        }
        .edgesIgnoringSafeArea(.top)
        .onChange(of: uiState.errorMessage) { errorMessage in
            if let errorMessage {
                toastManager.showError(title: "Error", message: errorMessage.message)
            }
        }
        .onChange(of: toastManager.toast) { newValue in
            if newValue == nil, let errorMessage = uiState.errorMessage {
                presenter.dispatch(action: ProfileActionMessageShown(id: errorMessage.id))
            }
        }
    }

    // MARK: - Authenticated Content

    private func profileScrollView(userProfile: ProfileInfo) -> some View {
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
                        .frame(height: theme.spacing.large)

                    statsSection(stats: userProfile.stats)

                    Spacer()
                        .frame(height: theme.spacing.xLarge)
                }
                .background(theme.colors.background)
                .offset(y: -10)
            }
        }
        .coordinateSpace(name: "profileScroll")
    }

    private func headerContent(userProfile: ProfileInfo, height: CGFloat) -> some View {
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
                    borderColor: theme.colors.accent,
                    borderWidth: 3
                )

                VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
                    Text(userProfile.fullName ?? userProfile.username)
                        .textStyle(theme.typography.titleLarge)
                        .foregroundColor(theme.colors.onPrimary)

                    Button(action: {}) {
                        Text(String(\.profile_edit_button))
                            .textStyle(theme.typography.labelMedium)
                            .foregroundColor(theme.colors.onPrimary)
                            .padding(.horizontal, 20)
                            .padding(.vertical, theme.spacing.xSmall)
                            .background(Color.clear)
                            .overlay(
                                RoundedRectangle(cornerRadius: theme.shapes.medium)
                                    .stroke(theme.colors.onPrimary, lineWidth: 1)
                            )
                    }
                }

                Spacer()
            }
            .padding(theme.spacing.medium)
        }
        .clipped()
    }

    // MARK: - Stats Section

    private func statsSection(stats: ProfileStats) -> some View {
        VStack(alignment: .leading, spacing: theme.spacing.medium) {
            HStack {
                ChevronTitle(
                    title: String(\.profile_stats_title),
                    chevronStyle: ChevronStyle.chevronOnly,
                    action: {}
                )
            }
            .padding(.horizontal, theme.spacing.medium)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .top, spacing: theme.spacing.small) {
                    StatsCardItem(
                        systemImage: "calendar",
                        title: String(\.profile_watch_time)
                    ) {
                        HStack(spacing: theme.spacing.large) {
                            statColumn(label: String(\.profile_time_months), value: stats.months)
                            statColumn(label: String(\.profile_time_days), value: stats.days)
                            statColumn(label: String(\.profile_time_hours), value: stats.hours)
                        }
                    }

                    StatsCardItem(
                        systemImage: "tv",
                        title: String(\.profile_episodes_watched)
                    ) {
                        VStack(spacing: 0) {
                            Text(formatNumber(stats.episodesWatched))
                                .textStyle(theme.typography.bodyMedium)
                                .foregroundColor(theme.colors.onSurface)
                                .frame(maxWidth: .infinity)
                        }
                        .padding(theme.spacing.xSmall)
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
            }
        }
    }

    private func statColumn(label: String, value: Int32) -> some View {
        VStack(spacing: theme.spacing.xxSmall) {
            Text("\(value)")
                .textStyle(theme.typography.titleMedium)
                .foregroundColor(theme.colors.onSurface)

            Text(label)
                .textStyle(theme.typography.bodySmall)
                .foregroundColor(theme.colors.onSurface)
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
            VStack(alignment: .leading, spacing: theme.spacing.large) {
                Spacer()
                    .frame(height: 84)

                Text(String(\.profile_unauthenticated_title))
                    .textStyle(theme.typography.headlineLarge)
                    .foregroundColor(theme.colors.onSurface)
                    .lineSpacing(theme.spacing.xSmall)
                    .padding(.horizontal, 28)

                VStack(alignment: .leading, spacing: theme.spacing.large) {
                    featureItem(
                        iconName: "magnifyingglass",
                        title: String(\.profile_feature_discover_title),
                        description: String(\.profile_feature_discover_description)
                    )

                    featureItem(
                        iconName: "tv",
                        title: String(\.profile_feature_track_title),
                        description: String(\.profile_feature_track_description)
                    )

                    featureItem(
                        iconName: "rectangle.stack",
                        title: String(\.profile_feature_manage_title),
                        description: String(\.profile_feature_manage_description)
                    )

                    featureItem(
                        iconName: "sparkles",
                        title: String(\.profile_feature_more_title),
                        description: String(\.profile_feature_more_description)
                    )
                }
                .padding(.horizontal, theme.spacing.large)

                Spacer()
                    .frame(height: theme.spacing.xSmall)

                VStack(spacing: 20) {
                    Text(String(\.profile_footer_description))
                        .textStyle(theme.typography.bodyMedium)
                        .foregroundColor(theme.colors.onSurface)
                        .lineSpacing(theme.spacing.xxSmall)
                        .padding(.horizontal, theme.spacing.large)

                    Button(action: {
                        presenter.dispatch(action: ProfileActionLoginClicked())
                    }) {
                        Text(String(\.profile_sign_in_button))
                            .textStyle(theme.typography.bodyMedium)
                            .foregroundColor(theme.colors.onButtonBackground)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, theme.spacing.medium)
                            .background(theme.colors.buttonBackground)
                            .cornerRadius(theme.shapes.extraLarge)
                    }
                    .padding(.horizontal, theme.spacing.large)
                }

                Spacer()
                    .frame(height: theme.spacing.xLarge)
            }
            .background(
                GeometryReader { geometry in
                    Color.clear.preference(
                        key: ScrollOffsetPreferenceKey.self,
                        value: geometry.frame(in: .named("scrollView")).minY
                    )
                }
            )
        }
        .coordinateSpace(name: "scrollView")
        .onPreferenceChange(ScrollOffsetPreferenceKey.self) { offset in
            DispatchQueue.main.async {
                showGlass = ParallaxConstants.glassOpacity(from: offset)
            }
        }
    }

    private func featureItem(iconName: String, title: String, description: String) -> some View {
        HStack(alignment: .top, spacing: theme.spacing.medium) {
            Image(systemName: iconName)
                .textStyle(theme.typography.headlineMedium)
                .foregroundColor(theme.colors.accent)
                .frame(width: 44, height: 44)

            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundColor(theme.colors.onSurface)

                Text(description)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundColor(theme.colors.onSurface)
                    .lineSpacing(2)
            }

            Spacer()
        }
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 350
}

struct ScrollOffsetPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
