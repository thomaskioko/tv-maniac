import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

struct DiscoverFeaturedSection: View {
    private let presenter: DiscoverFeaturedPresenter
    @StateValue private var state: DiscoverFeaturedState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var currentIndex: Int
    @State private var selectedShow: SwiftShow?
    @State private var isDraggingCarousel: Bool = false

    init(presenter: DiscoverFeaturedPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
        _currentIndex = State(initialValue: SettingsAppStorage.shared.savedIndex)
    }

    var body: some View {
        DiscoverFeaturedContent(
            shows: state.featuredShowsSwift,
            currentIndex: $currentIndex,
            selectedShow: $selectedShow,
            isDraggingCarousel: $isDraggingCarousel,
            onShowClicked: { id in
                presenter.dispatch(action: FeaturedShowClicked(showId: id))
            },
            onIndexChanged: { index in
                store.savedIndex = index
            }
        )
    }
}

struct DiscoverFeaturedContent: View {
    @Environment(\.appTheme) private var appTheme

    let shows: [SwiftShow]
    @Binding var currentIndex: Int
    @Binding var selectedShow: SwiftShow?
    @Binding var isDraggingCarousel: Bool
    let onShowClicked: (Int64) -> Void
    let onIndexChanged: (Int) -> Void

    var body: some View {
        GeometryReader { proxy in
            let scrollY = proxy.frame(in: .named("discoverScroll")).minY

            headerContent
                .frame(width: proxy.size.width, height: CarouselConstants.headerHeight + max(scrollY, 0))
                .offset(y: -max(scrollY, 0))
                .overlay(alignment: .bottom) {
                    FeaturedInfoOverlay(
                        selectedShow: selectedShow,
                        shows: shows,
                        currentIndex: currentIndex,
                        isDraggingCarousel: isDraggingCarousel
                    )
                }
                .preference(key: DiscoverScrollOffsetKey.self, value: scrollY)
        }
        .frame(height: CarouselConstants.headerHeight)
    }

    @ViewBuilder
    private var headerContent: some View {
        if shows.isEmpty {
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle())
                .scaleEffect(1.5)
                .tint(appTheme.colors.accent)
        } else {
            CarouselView(
                items: shows,
                currentIndex: $currentIndex,
                onItemScrolled: { item in
                    selectedShow = item
                    onIndexChanged(currentIndex)
                },
                onDraggingChanged: { isDragging in
                    isDraggingCarousel = isDragging
                }
            ) { index in
                carouselItemView(item: shows[index])
            }
        }
    }

    private func carouselItemView(item: SwiftShow) -> some View {
        GeometryReader { geometry in
            PosterItemView(
                title: item.title,
                posterUrl: item.posterUrl,
                imageType: .backdrop,
                posterWidth: geometry.size.width,
                posterHeight: geometry.size.height,
                processorHeight: CarouselConstants.fixedImageHeight
            )
            .onTapGesture {
                onShowClicked(item.showId)
            }
        }
    }

    enum CarouselConstants {
        static let headerHeight: CGFloat = 580
        static let fixedImageHeight: CGFloat = headerHeight
    }
}

public struct DiscoverScrollOffsetKey: PreferenceKey {
    public static let defaultValue: CGFloat = 0

    public static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

struct FeaturedInfoOverlay: View {
    @Environment(\.appTheme) private var appTheme

    let selectedShow: SwiftShow?
    let shows: [SwiftShow]
    let currentIndex: Int
    let isDraggingCarousel: Bool

    var body: some View {
        VStack(alignment: .leading) {
            Text(selectedShow?.title ?? "")
                .textStyle(appTheme.typography.headlineLarge)
                .foregroundStyle(.appOnSurface)
                .lineLimit(1)
                .frame(maxWidth: .infinity, alignment: .center)

            if let overview = selectedShow?.overview {
                Text(overview)
                    .textStyle(appTheme.typography.bodyLarge)
                    .foregroundStyle(.appOnSurface.opacity(0.9))
                    .multilineTextAlignment(.leading)
                    .lineLimit(4)
            }

            customIndicator
                .padding(.top, appTheme.spacing.xSmall)
                .frame(maxWidth: .infinity, alignment: .center)
        }
        .padding(.horizontal)
        .padding(.bottom, appTheme.spacing.medium)
        .frame(maxWidth: .infinity, alignment: .leading)
        .allowsHitTesting(false)
        .background(
            LinearGradient(
                stops: [
                    .init(color: appTheme.colors.background, location: 0),
                    .init(color: appTheme.colors.background, location: 0.3),
                    .init(color: appTheme.colors.background.opacity(0.9), location: 0.5),
                    .init(color: appTheme.colors.background.opacity(0.7), location: 0.65),
                    .init(color: appTheme.colors.background.opacity(0.4), location: 0.8),
                    .init(color: .clear, location: 1.0),
                ],
                startPoint: .bottom,
                endPoint: .top
            )
            .padding(.top, -120)
            .allowsHitTesting(false)
        )
    }

    private var customIndicator: some View {
        ZStack {
            Color.clear
                .frame(height: 10)

            CircularIndicator(
                totalItems: shows.count,
                currentIndex: currentIndex,
                isDragging: isDraggingCarousel
            )
            .allowsHitTesting(false)
            .transaction { transaction in
                transaction.animation = nil
            }
        }
    }
}

#Preview("Featured - Empty") {
    ScrollView {
        DiscoverFeaturedContent(
            shows: [],
            currentIndex: .constant(0),
            selectedShow: .constant(nil),
            isDraggingCarousel: .constant(false),
            onShowClicked: { _ in },
            onIndexChanged: { _ in }
        )
    }
    .appPreview()
}

#Preview("Featured - Content") {
    let sampleShows: [SwiftShow] = [
        SwiftShow(
            showId: 1,
            title: "Breaking Bad",
            posterUrl: nil,
            backdropUrl: nil,
            inLibrary: false,
            overview: "A chemistry teacher diagnosed with cancer turns to manufacturing methamphetamine."
        ),
        SwiftShow(
            showId: 2,
            title: "Game of Thrones",
            posterUrl: nil,
            backdropUrl: nil,
            inLibrary: true,
            overview: "Nine noble families fight for control of the lands of Westeros."
        ),
    ]
    ScrollView {
        DiscoverFeaturedContent(
            shows: sampleShows,
            currentIndex: .constant(0),
            selectedShow: .constant(sampleShows.first),
            isDraggingCarousel: .constant(false),
            onShowClicked: { _ in },
            onIndexChanged: { _ in }
        )
    }
    .appPreview()
}
