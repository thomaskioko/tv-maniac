import Components
import DesignSystem
import Models
import SwiftUI

public struct MoreShowsScreen: View {
    public struct State: Equatable {
        public let title: String
        public let items: [ShowPosterImage]
        public let isLoadingMore: Bool
        public let hasNextPage: Bool
        public let loadError: String?
        public let retryLabel: String

        public init(
            title: String,
            items: [ShowPosterImage],
            isLoadingMore: Bool,
            hasNextPage: Bool,
            loadError: String?,
            retryLabel: String
        ) {
            self.title = title
            self.items = items
            self.isLoadingMore = isLoadingMore
            self.hasNextPage = hasNextPage
            self.loadError = loadError
            self.retryLabel = retryLabel
        }
    }

    @Environment(\.appTheme) private var theme

    private let state: State
    @Binding private var toast: Toast?
    private let onItemAppear: (Int) -> Void
    private let onLoadMore: () -> Void
    private let onAction: (Int64) -> Void
    private let onBack: () -> Void
    private let onRetry: () -> Void

    public init(
        state: State,
        toast: Binding<Toast?>,
        onItemAppear: @escaping (Int) -> Void,
        onLoadMore: @escaping () -> Void,
        onAction: @escaping (Int64) -> Void,
        onBack: @escaping () -> Void,
        onRetry: @escaping () -> Void
    ) {
        self.state = state
        _toast = toast
        self.onItemAppear = onItemAppear
        self.onLoadMore = onLoadMore
        self.onAction = onAction
        self.onBack = onBack
        self.onRetry = onRetry
    }

    @SwiftUI.State private var scrollPosition: Int64?
    @Environment(\.widthSizeClass) private var widthSizeClass

    public var body: some View {
        ScrollView(.vertical, showsIndicators: false) {
            LazyVGrid(
                columns: ImageDimens.posterGridColumns(widthSizeClass, spacing: ImageDimens.gridItemSpacing),
                spacing: ImageDimens.gridItemSpacing
            ) {
                ForEach(state.items) { item in
                    PosterItemView(
                        title: item.title,
                        posterUrl: item.posterUrl,
                        posterWidth: 130,
                        posterHeight: 200
                    )
                    .aspectRatio(contentMode: .fill)
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                    .clipped()
                    .onTapGesture { onAction(item.traktId) }
                    .onAppear {
                        if let index = state.items.firstIndex(of: item) {
                            onItemAppear(index)
                            if state.hasNextPage, index >= state.items.count - 6 {
                                onLoadMore()
                            }
                        }
                    }
                }
            }
            .scrollTargetLayout()
            .padding(.all, theme.spacing.xSmall)

            if !state.items.isEmpty, state.hasNextPage {
                ProgressView()
                    .tint(theme.colors.secondary)
                    .padding(theme.spacing.large)
                    .frame(maxWidth: .infinity)
                    .id(state.items.count)
                    .onAppear {
                        onLoadMore()
                    }
            }

            if let loadError = state.loadError {
                VStack(spacing: theme.spacing.small) {
                    Text(loadError)
                        .textStyle(theme.typography.bodySmall)
                        .foregroundStyle(.appError)
                        .multilineTextAlignment(.center)

                    Button(action: onRetry) {
                        Text(state.retryLabel)
                            .textStyle(theme.typography.labelLarge)
                            .foregroundStyle(.appOnPrimary)
                            .padding(.horizontal, theme.spacing.medium)
                            .padding(.vertical, theme.spacing.xSmall)
                            .background(.appSecondary)
                            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
                    }
                }
                .padding(theme.spacing.medium)
                .frame(maxWidth: .infinity)
            }
        }
        .scrollPosition(id: $scrollPosition)
        .onChange(of: scrollPosition) { _, newPosition in
            guard let newPosition, state.hasNextPage, !state.isLoadingMore else { return }
            if let index = state.items.firstIndex(where: { $0.traktId == newPosition }),
               index >= state.items.count - 6
            {
                onLoadMore()
            }
        }
        .contentMargins(.top, toolbarInset + theme.spacing.medium)
        .appScreen()
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(
            GlassToolbar(
                title: state.title,
                opacity: 1.0,
                leadingIcon: {
                    GlassButton(icon: "chevron.left", action: onBack)
                }
            ),
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
        .toastView(toast: $toast)
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}

#Preview("More Shows") {
    MoreShowsScreen(
        state: MoreShowsScreen.State(
            title: "Popular",
            items: [
                .init(traktId: 1, title: "Arcane", posterUrl: nil),
                .init(traktId: 2, title: "Loki", posterUrl: nil),
                .init(traktId: 3, title: "The Bear", posterUrl: nil),
                .init(traktId: 4, title: "Severance", posterUrl: nil),
                .init(traktId: 5, title: "Shogun", posterUrl: nil),
                .init(traktId: 6, title: "Fallout", posterUrl: nil),
            ],
            isLoadingMore: false,
            hasNextPage: false,
            loadError: nil,
            retryLabel: "Retry"
        ),
        toast: .constant(nil),
        onItemAppear: { _ in },
        onLoadMore: {},
        onAction: { _ in },
        onBack: {},
        onRetry: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("More Shows - Loading More") {
    MoreShowsScreen(
        state: MoreShowsScreen.State(
            title: "Trending",
            items: [
                .init(traktId: 1, title: "Arcane", posterUrl: nil),
                .init(traktId: 2, title: "Loki", posterUrl: nil),
                .init(traktId: 3, title: "The Bear", posterUrl: nil),
            ],
            isLoadingMore: true,
            hasNextPage: true,
            loadError: nil,
            retryLabel: "Retry"
        ),
        toast: .constant(nil),
        onItemAppear: { _ in },
        onLoadMore: {},
        onAction: { _ in },
        onBack: {},
        onRetry: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}
