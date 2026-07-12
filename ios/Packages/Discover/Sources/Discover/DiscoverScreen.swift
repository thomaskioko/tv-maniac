import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

public struct DiscoverScreen: View {
    public struct State {
        public let title: String
        public let isLoading: Bool
        public let isEmpty: Bool
        public let showError: Bool
        public let errorMessage: String?
        public let isRefreshing: Bool
        public let emptyContentText: String
        public let missingApiKeyText: String
        public let retryText: String

        public init(
            title: String,
            isLoading: Bool,
            isEmpty: Bool,
            showError: Bool,
            errorMessage: String?,
            isRefreshing: Bool,
            emptyContentText: String,
            missingApiKeyText: String,
            retryText: String
        ) {
            self.title = title
            self.isLoading = isLoading
            self.isEmpty = isEmpty
            self.showError = showError
            self.errorMessage = errorMessage
            self.isRefreshing = isRefreshing
            self.emptyContentText = emptyContentText
            self.missingApiKeyText = missingApiKeyText
            self.retryText = retryText
        }
    }

    @Environment(\.appTheme) private var appTheme

    private let state: State
    private let presenter: DiscoverShowsPresenter
    @Binding private var toast: Toast?
    private let onSearchClicked: () -> Void
    private let onRefresh: () -> Void

    @SwiftUI.State private var showGlass: Double = 0
    @SwiftUI.State private var pullOffset: CGFloat = 0
    @SwiftUI.State private var isRefreshingLocal: Bool = false
    @SwiftUI.State private var isScrollInteracting: Bool = false

    public init(
        state: State,
        presenter: DiscoverShowsPresenter,
        toast: Binding<Toast?>,
        onSearchClicked: @escaping () -> Void,
        onRefresh: @escaping () -> Void
    ) {
        self.state = state
        self.presenter = presenter
        _toast = toast
        self.onSearchClicked = onSearchClicked
        self.onRefresh = onRefresh
    }

    public var body: some View {
        Group {
            if state.isLoading {
                LoadingIndicatorView()
            } else if state.isEmpty {
                emptyView
            } else if state.showError {
                EmptyStateView(
                    systemName: "exclamationmark.arrow.triangle.2.circlepath",
                    title: state.errorMessage ?? "Something went wrong"
                )
            } else {
                discoverLoadedContent
            }
        }
        .appScreen()
    }

    private var discoverLoadedContent: some View {
        ZStack(alignment: .top) {
            discoverScrollView

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

            if #available(iOS 18.0, *) {
                let progress = min(pullOffset / RefreshConstants.threshold, 1.0)

                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: appTheme.colors.onSurface))
                    .scaleEffect(2.0)
                    .opacity(pullOffset > 0 ? max(0.6, Double(progress)) : 0)
                    .padding(.top, RefreshConstants.indicatorTopPadding)
                    .allowsHitTesting(false)
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .toolbar(.hidden, for: .navigationBar)
        .overlay(
            GlassToolbar(
                title: state.title,
                opacity: showGlass,
                isLoading: false,
                trailingIcon: {
                    GlassButton(icon: "magnifyingglass", action: onSearchClicked)
                }
            )
            .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass),
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
        .onDisappear {
            showGlass = 0
        }
        .onChange(of: state.isRefreshing) { _, newValue in
            if !newValue, isRefreshingLocal {
                withAnimation(.easeOut(duration: AnimationConstants.defaultDuration)) {
                    isRefreshingLocal = false
                }
            }
        }
        .toastView(toast: $toast)
    }

    @ViewBuilder
    private var discoverScrollView: some View {
        if #available(iOS 18.0, *) {
            scrollViewContent
                .onScrollGeometryChange(for: CGFloat.self) { geometry in
                    geometry.contentOffset.y
                } action: { _, newValue in
                    if !isRefreshingLocal, isScrollInteracting {
                        pullOffset = max(0, -newValue)
                    }
                }
                .onScrollPhaseChange { oldPhase, newPhase in
                    isScrollInteracting = newPhase == .interacting
                    if oldPhase == .interacting {
                        if !isRefreshingLocal, pullOffset >= RefreshConstants.threshold {
                            isRefreshingLocal = true
                            onRefresh()
                        }
                        pullOffset = 0
                    }
                }
        } else {
            scrollViewContent
        }
    }

    private var scrollViewContent: some View {
        ScrollView(showsIndicators: false) {
            VStack(spacing: 0) {
                DiscoverFeaturedSection(presenter: presenter.featuredPresenter)

                DiscoverSectionsContent(presenter: presenter)
            }
        }
        .coordinateSpace(name: "discoverScroll")
        .onPreferenceChange(DiscoverScrollOffsetKey.self) { value in
            DispatchQueue.main.async {
                showGlass = value < 0
                    ? ParallaxConstants.glassOpacity(from: value)
                    : 0
            }
        }
    }

    private var emptyView: some View {
        EmptyStateView(
            systemName: "list.bullet.below.rectangle",
            title: state.emptyContentText,
            message: state.missingApiKeyText,
            buttonText: state.retryText,
            action: onRefresh
        )
    }

    private enum RefreshConstants {
        static let threshold: CGFloat = 80
        static let indicatorTopPadding: CGFloat = 100
    }
}

private struct DiscoverSectionsContent: View {
    @Environment(\.appTheme) private var appTheme
    let presenter: DiscoverShowsPresenter
    @StateValue private var startWatchingState: DiscoverStartWatchingState

    init(presenter: DiscoverShowsPresenter) {
        self.presenter = presenter
        _startWatchingState = .init(presenter.startWatchingPresenter.stateValue)
    }

    var body: some View {
        VStack {
            DiscoverUpNextSection(presenter: presenter.upNextPresenter)
            if startWatchingState.startWatchingVisible {
                DiscoverStartWatchingSection(presenter: presenter.startWatchingPresenter)
            }
            DiscoverCatalogSection(presenter: presenter.catalogPresenter)
        }
        .padding(.top, appTheme.spacing.medium)
        .background(.appBackground)
        .offset(y: -10)
    }
}

#Preview("Loading") {
    DiscoverScreenScaffoldPreview(
        state: DiscoverScreen.State(
            title: "Discover",
            isLoading: true,
            isEmpty: false,
            showError: false,
            errorMessage: nil,
            isRefreshing: false,
            emptyContentText: "No content available",
            missingApiKeyText: "API key missing",
            retryText: "Retry"
        )
    )
    .appPreview()
}

#Preview("Empty") {
    DiscoverScreenScaffoldPreview(
        state: DiscoverScreen.State(
            title: "Discover",
            isLoading: false,
            isEmpty: true,
            showError: false,
            errorMessage: nil,
            isRefreshing: false,
            emptyContentText: "No content available",
            missingApiKeyText: "API key missing",
            retryText: "Retry"
        )
    )
    .appPreview()
}

#Preview("Error") {
    DiscoverScreenScaffoldPreview(
        state: DiscoverScreen.State(
            title: "Discover",
            isLoading: false,
            isEmpty: false,
            showError: true,
            errorMessage: "Something went wrong",
            isRefreshing: false,
            emptyContentText: "No content available",
            missingApiKeyText: "API key missing",
            retryText: "Retry"
        )
    )
    .appPreview()
}

private struct DiscoverScreenScaffoldPreview: View {
    let state: DiscoverScreen.State

    var body: some View {
        Group {
            if state.isLoading {
                LoadingIndicatorView()
            } else if state.isEmpty {
                EmptyStateView(
                    systemName: "list.bullet.below.rectangle",
                    title: state.emptyContentText,
                    message: state.missingApiKeyText,
                    buttonText: state.retryText,
                    action: {}
                )
            } else if state.showError {
                EmptyStateView(
                    systemName: "exclamationmark.arrow.triangle.2.circlepath",
                    title: state.errorMessage ?? "Something went wrong"
                )
            }
        }
        .appScreen()
    }
}
