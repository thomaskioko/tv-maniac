import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ProfileTab: View {
    private let presenter: ProfilePresenter
    @State private var showGlass: Double = 0
    @State private var progressViewOffset: CGFloat = 0
    @StateObject @KotlinStateFlow private var uiState: ProfileState

    init(presenter: ProfilePresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ZStack {
            Color.background
                .edgesIgnoringSafeArea(.all)

            if uiState.isLoading, uiState.userProfile == nil {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .accent))
                    .scaleEffect(1.5)
            } else if let userProfile = uiState.userProfile {
                profileContent(userProfile: userProfile)
            } else {
                unauthenticatedContent()
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .navigationBarBackButtonHidden(true)
        .overlay(
            GlassToolbar(
                title: String(\.profile_title),
                opacity: showGlass,
                trailingIcon: {
                    Button(
                        action: {
                            presenter.dispatch(action: ProfileActionSettingsClicked())
                        }
                    ) {
                        Image(systemName: "gearshape")
                            .font(.system(size: 24))
                            .foregroundColor(.textColor)
                    }
                }
            ),
            alignment: .top
        )
        .animation(.easeInOut(duration: 0.25), value: showGlass)
        .edgesIgnoringSafeArea(.top)
        .alert(isPresented: .constant(uiState.errorMessage != nil)) {
            Alert(
                title: Text("Error"),
                message: Text(uiState.errorMessage?.message ?? ""),
                dismissButton: .default(Text("OK")) {
                    presenter.dispatch(action: ProfileActionMessageShown(id: uiState.errorMessage?.id ?? 0))
                }
            )
        }
    }

    @ViewBuilder
    private func profileContent(userProfile: ProfileInfo) -> some View {
        ParallaxView(
            imageHeight: DimensionConstants.imageHeight,
            collapsedImageHeight: DimensionConstants.collapsedImageHeight,
            header: { proxy in
                HeaderContent(
                    userProfile: userProfile,
                    progress: proxy.getTitleOpacity(
                        geometry: proxy,
                        imageHeight: DimensionConstants.imageHeight,
                        collapsedImageHeight: DimensionConstants.collapsedImageHeight
                    ),
                    headerHeight: proxy.getHeightForHeaderImage(proxy)
                )
            },
            content: {
                VStack(spacing: 0) {
                    Spacer()
                        .frame(height: 24)

                    statsSection(stats: userProfile.stats)

                    Spacer()
                        .frame(height: 32)
                }
            },
            onScroll: { offset in
                let opacity = -offset - 150
                let normalizedOpacity = opacity / 200
                showGlass = max(0, min(1, normalizedOpacity))
            }
        )
    }

    @ViewBuilder
    private func HeaderContent(userProfile: ProfileInfo, progress: CGFloat, headerHeight: CGFloat) -> some View {
        ZStack(alignment: .bottom) {
            // Background image
            HeaderCoverArtWorkView(
                imageUrl: userProfile.backgroundUrl,
                posterHeight: headerHeight
            )
            .foregroundStyle(.ultraThinMaterial)
            .overlay(
                LinearGradient(
                    gradient: Gradient(colors: [
                        .clear,
                        .clear,
                        .clear,
                        .clear,
                        .black.opacity(0.6),
                        .black.opacity(0.8),
                        .black,
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
            .frame(height: headerHeight)

            ZStack(alignment: .bottom) {
                VStack {
                    Spacer()

                    HStack(alignment: .center, spacing: 16) {
                        // Avatar
                        AvatarView(
                            avatarUrl: userProfile.avatarUrl,
                            size: 80,
                            borderColor: .accent,
                            borderWidth: 3
                        )

                        // Username and Edit button
                        VStack(alignment: .leading, spacing: 8) {
                            Text(userProfile.fullName ?? userProfile.username)
                                .font(.avenirNext(size: 20))
                                .fontWeight(.bold)
                                .foregroundColor(.white)

                            Button(action: {
                                // Edit action - empty for now
                            }) {
                                Text(String(\.profile_edit_button))
                                    .font(.avenirNext(size: 12))
                                    .fontWeight(.semibold)
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 20)
                                    .padding(.vertical, 8)
                                    .background(Color.clear)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 8)
                                            .stroke(Color.white, lineWidth: 1)
                                    )
                            }
                        }

                        Spacer()
                    }
                    .padding(16)
                }
                .frame(height: headerHeight)
            }
            .opacity(1 - progress)
        }
        .frame(height: headerHeight)
        .clipped()
    }

    @ViewBuilder
    private func statsSection(stats: ProfileStats) -> some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                ChevronTitle(
                    title: String(\.profile_stats_title),
                    chevronStyle: ChevronStyle.chevronOnly,
                    action: {}
                )
            }
            .padding(.horizontal, 16)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .top, spacing: 12) {
                    // Watch Time Card
                    StatsCardItem(
                        systemImage: "calendar",
                        title: String(\.profile_watch_time)
                    ) {
                        HStack(spacing: 24) {
                            statColumn(label: String(\.profile_time_months), value: stats.months)
                            statColumn(label: String(\.profile_time_days), value: stats.days)
                            statColumn(label: String(\.profile_time_hours), value: stats.hours)
                        }
                    }

                    // Episodes Watched Card
                    StatsCardItem(
                        systemImage: "tv",
                        title: String(\.profile_episodes_watched)
                    ) {
                        VStack(spacing: 0) {
                            Text(formatNumber(stats.episodesWatched))
                                .font(.avenirNext(size: 16))
                                .fontWeight(.medium)
                                .foregroundColor(.textColor)
                                .frame(maxWidth: .infinity)
                        }
                        .padding(10)
                    }
                }
                .padding(.horizontal, 16)
            }
        }
    }

    @ViewBuilder
    private func statColumn(label: String, value: Int32) -> some View {
        VStack(spacing: 4) {
            Text("\(value)")
                .font(.avenirNext(size: 18))
                .fontWeight(.medium)
                .foregroundColor(.textColor)

            Text(label)
                .font(.avenirNext(size: 12))
                .fontWeight(.medium)
                .foregroundColor(.textColor)
        }
    }

    private func formatNumber(_ number: Int32) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.groupingSeparator = ","
        return formatter.string(from: NSNumber(value: number)) ?? "\(number)"
    }

    @ViewBuilder
    private func unauthenticatedContent() -> some View {
        ScrollView(.vertical, showsIndicators: false) {
            VStack(alignment: .leading, spacing: 24) {
                Spacer()
                    .frame(height: 84)

                Text(String(\.profile_unauthenticated_title))
                    .font(.system(size: 32, weight: .bold))
                    .foregroundColor(.textColor)
                    .lineSpacing(8)
                    .padding(.horizontal, 28)

                VStack(alignment: .leading, spacing: 24) {
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
                .padding(.horizontal, 24)

                Spacer()
                    .frame(height: 8)

                VStack(spacing: 20) {
                    Text(String(\.profile_footer_description))
                        .font(.avenirNext(size: 16))
                        .foregroundColor(.textColor)
                        .lineSpacing(4)
                        .padding(.horizontal, 24)

                    Button(action: {
                        presenter.dispatch(action: ProfileActionLoginClicked())
                    }) {
                        Text(String(\.profile_sign_in_button))
                            .font(.avenirNext(size: 16))
                            .fontWeight(.semibold)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(Color.buttonColor)
                            .cornerRadius(25)
                    }
                    .padding(.horizontal, 24)
                }

                Spacer()
                    .frame(height: 32)
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
            let opacity = -offset - 150
            let normalizedOpacity = opacity / 200
            showGlass = max(0, min(1, normalizedOpacity))
        }
    }

    @ViewBuilder
    private func featureItem(iconName: String, title: String, description: String) -> some View {
        HStack(alignment: .top, spacing: 16) {
            Image(systemName: iconName)
                .font(.system(size: 28))
                .foregroundColor(.accent)
                .frame(width: 44, height: 44)

            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.avenirNext(size: 18))
                    .fontWeight(.semibold)
                    .foregroundColor(.textColor)

                Text(description)
                    .font(.avenirNext(size: 15))
                    .foregroundColor(.textColor)
                    .lineSpacing(2)
            }

            Spacer()
        }
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 350
    static let collapsedImageHeight: CGFloat = 120.0
}

struct ScrollOffsetPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
