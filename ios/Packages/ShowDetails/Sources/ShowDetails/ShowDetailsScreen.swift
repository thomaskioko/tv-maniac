import Components
import DesignSystem
import Models
import SwiftUI

public struct ShowDetailsScreen<Content: View>: View {
    public struct State {
        public let title: String
        public let overview: String
        public let backdropImageUrl: String?
        public let status: String?
        public let year: String
        public let language: String?
        public let communityRating: Double?
        public let communityVotes: Int64?
        public let userRating: Int?
        public let numberOfSeasons: Int
        public let isRefreshing: Bool

        public init(
            title: String,
            overview: String,
            backdropImageUrl: String?,
            status: String?,
            year: String,
            language: String?,
            communityRating: Double?,
            communityVotes: Int64?,
            userRating: Int?,
            numberOfSeasons: Int,
            isRefreshing: Bool
        ) {
            self.title = title
            self.overview = overview
            self.backdropImageUrl = backdropImageUrl
            self.status = status
            self.year = year
            self.language = language
            self.communityRating = communityRating
            self.communityVotes = communityVotes
            self.userRating = userRating
            self.numberOfSeasons = numberOfSeasons
            self.isRefreshing = isRefreshing
        }
    }

    @Environment(\.appTheme) private var appTheme

    private let state: State
    @Binding private var toast: Toast?
    private let content: Content
    private let seasonCountFormat: (Int) -> String
    private let onBack: () -> Void
    private let onRefresh: () -> Void

    @SwiftUI.State private var showGlass: Double = 0

    public init(
        state: State,
        toast: Binding<Toast?>,
        seasonCountFormat: @escaping (Int) -> String,
        onBack: @escaping () -> Void,
        onRefresh: @escaping () -> Void,
        @ViewBuilder content: () -> Content
    ) {
        self.state = state
        _toast = toast
        self.seasonCountFormat = seasonCountFormat
        self.onBack = onBack
        self.onRefresh = onRefresh
        self.content = content()
    }

    public var body: some View {
        ParallaxView(
            imageHeight: DimensionConstants.imageHeight,
            collapsedImageHeight: DimensionConstants.collapsedImageHeight,
            header: { proxy in
                HeaderView(
                    title: state.title,
                    overview: state.overview,
                    backdropImageUrl: state.backdropImageUrl,
                    status: state.status,
                    year: state.year,
                    language: state.language,
                    communityRating: state.communityRating,
                    communityVotes: state.communityVotes,
                    seasonCount: state.numberOfSeasons,
                    seasonCountFormat: seasonCountFormat,
                    progress: proxy.getTitleOpacity(
                        geometry: proxy,
                        imageHeight: DimensionConstants.imageHeight,
                        collapsedImageHeight: DimensionConstants.collapsedImageHeight
                    ),
                    headerHeight: proxy.getHeightForHeaderImage(proxy)
                )
            },
            content: { content },
            onScroll: { offset in
                let newValue = ParallaxConstants.glassOpacity(from: offset, triggerOffset: 170, divisor: 220)
                if abs(newValue - showGlass) > 0.02 {
                    showGlass = newValue
                }
            }
        )
        .appScreen()
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .navigationBarBackButtonHidden(true)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(
            GlassToolbar(
                title: state.title,
                opacity: showGlass,
                isLoading: state.isRefreshing,
                leadingIcon: {
                    GlassButton(icon: "chevron.left", action: onBack)
                        .opacity(1 - showGlass)
                },
                trailingIcon: {
                    GlassButton(icon: "arrow.clockwise", action: onRefresh)
                }
            )
            .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass),
            alignment: .top
        )
        .coordinateSpace(name: CoordinateSpaces.scrollView)
        .edgesIgnoringSafeArea(.top)
        .toastView(toast: $toast)
    }

    private enum CoordinateSpaces {
        case scrollView
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 500
    static let collapsedImageHeight: CGFloat = 120.0
}

#Preview {
    ShowDetailsScreen(
        state: ShowDetailsScreen.State(
            title: "Arcane",
            overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            backdropImageUrl: nil,
            status: "Ended",
            year: "2024",
            language: "EN",
            communityRating: 4.8,
            communityVotes: 12500,
            userRating: 9,
            numberOfSeasons: 2,
            isRefreshing: false
        ),
        toast: .constant(nil),
        seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" },
        onBack: {},
        onRefresh: {}
    ) {
        EmptyView()
    }
    .appPreview()
}
