import SwiftUI

public struct MoreShowsScreen: View {
    @Theme private var theme

    private let title: String
    private let items: [ShowPosterImage]
    private let isLoadingMore: Bool
    private let loadError: String?
    @Binding private var toast: Toast?
    private let onItemAppear: (Int) -> Void
    private let onAction: (Int64) -> Void
    private let onBack: () -> Void
    private let onRetry: () -> Void

    public init(
        title: String,
        items: [ShowPosterImage],
        isLoadingMore: Bool,
        loadError: String?,
        toast: Binding<Toast?>,
        onItemAppear: @escaping (Int) -> Void,
        onAction: @escaping (Int64) -> Void,
        onBack: @escaping () -> Void,
        onRetry: @escaping () -> Void
    ) {
        self.title = title
        self.items = items
        self.isLoadingMore = isLoadingMore
        self.loadError = loadError
        _toast = toast
        self.onItemAppear = onItemAppear
        self.onAction = onAction
        self.onBack = onBack
        self.onRetry = onRetry
    }

    @State private var scrollPosition: Int64?

    private let columns = [GridItem(.adaptive(minimum: 100), spacing: 4)]

    public var body: some View {
        ScrollView(.vertical, showsIndicators: false) {
            LazyVGrid(columns: columns, spacing: theme.spacing.xxSmall) {
                ForEach(items) { item in
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
                        if let index = items.firstIndex(of: item) {
                            onItemAppear(index)
                        }
                    }
                }
            }
            .scrollTargetLayout()
            .padding(.all, theme.spacing.xSmall)

            if isLoadingMore {
                ProgressView()
                    .tint(theme.colors.secondary)
                    .padding(theme.spacing.large)
                    .frame(maxWidth: .infinity)
            }

            if let loadError {
                VStack(spacing: theme.spacing.small) {
                    Text(loadError)
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.error)
                        .multilineTextAlignment(.center)

                    Button(action: onRetry) {
                        Text("Retry")
                            .textStyle(theme.typography.labelLarge)
                            .foregroundColor(theme.colors.onPrimary)
                            .padding(.horizontal, theme.spacing.medium)
                            .padding(.vertical, theme.spacing.xSmall)
                            .background(theme.colors.secondary)
                            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
                    }
                }
                .padding(theme.spacing.medium)
                .frame(maxWidth: .infinity)
            }
        }
        .scrollPosition(id: $scrollPosition)
        .contentMargins(.top, toolbarInset + theme.spacing.medium)
        .background(theme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(
            GlassToolbar(
                title: title,
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
    ThemedPreview {
        MoreShowsScreen(
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
            loadError: nil,
            toast: .constant(nil),
            onItemAppear: { _ in },
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
    }
    .preferredColorScheme(.dark)
}

#Preview("More Shows - Loading More") {
    ThemedPreview {
        MoreShowsScreen(
            title: "Trending",
            items: [
                .init(traktId: 1, title: "Arcane", posterUrl: nil),
                .init(traktId: 2, title: "Loki", posterUrl: nil),
                .init(traktId: 3, title: "The Bear", posterUrl: nil),
            ],
            isLoadingMore: true,
            loadError: nil,
            toast: .constant(nil),
            onItemAppear: { _ in },
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
    }
    .preferredColorScheme(.dark)
}
