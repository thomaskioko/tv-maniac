import Components
import DesignSystem
import Models
import SwiftUI

public struct ListDetailScreen: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private let state: State
    private let backButtonAccessibilityLabel: String
    private let moreOptionsAccessibilityLabel: String
    private let onBack: () -> Void
    private let onItemAppear: (Int) -> Void
    private let onLoadMore: () -> Void
    private let onRefresh: () -> Void
    private let onShowClicked: (Int64) -> Void
    private let onRemoveRequested: (Int64) -> Void
    private let onRemoveConfirmed: () -> Void
    private let onRemoveDismissed: () -> Void
    private let onRetryLoadMore: () -> Void
    private let onDismissErrorMessage: () -> Void
    private let onRenameRequested: () -> Void
    private let onRenameNameChanged: (String) -> Void
    private let onRenameConfirmed: () -> Void
    private let onRenameDismissed: () -> Void
    private let onDeleteRequested: () -> Void
    private let onDeleteConfirmed: () -> Void
    private let onDeleteDismissed: () -> Void

    public init(
        state: State,
        backButtonAccessibilityLabel: String = "",
        moreOptionsAccessibilityLabel: String = "",
        onBack: @escaping () -> Void = {},
        onItemAppear: @escaping (Int) -> Void = { _ in },
        onLoadMore: @escaping () -> Void = {},
        onRefresh: @escaping () -> Void = {},
        onShowClicked: @escaping (Int64) -> Void = { _ in },
        onRemoveRequested: @escaping (Int64) -> Void = { _ in },
        onRemoveConfirmed: @escaping () -> Void = {},
        onRemoveDismissed: @escaping () -> Void = {},
        onRetryLoadMore: @escaping () -> Void = {},
        onDismissErrorMessage: @escaping () -> Void = {},
        onRenameRequested: @escaping () -> Void = {},
        onRenameNameChanged: @escaping (String) -> Void = { _ in },
        onRenameConfirmed: @escaping () -> Void = {},
        onRenameDismissed: @escaping () -> Void = {},
        onDeleteRequested: @escaping () -> Void = {},
        onDeleteConfirmed: @escaping () -> Void = {},
        onDeleteDismissed: @escaping () -> Void = {}
    ) {
        self.state = state
        self.backButtonAccessibilityLabel = backButtonAccessibilityLabel
        self.moreOptionsAccessibilityLabel = moreOptionsAccessibilityLabel
        self.onBack = onBack
        self.onItemAppear = onItemAppear
        self.onLoadMore = onLoadMore
        self.onRefresh = onRefresh
        self.onShowClicked = onShowClicked
        self.onRemoveRequested = onRemoveRequested
        self.onRemoveConfirmed = onRemoveConfirmed
        self.onRemoveDismissed = onRemoveDismissed
        self.onRetryLoadMore = onRetryLoadMore
        self.onDismissErrorMessage = onDismissErrorMessage
        self.onRenameRequested = onRenameRequested
        self.onRenameNameChanged = onRenameNameChanged
        self.onRenameConfirmed = onRenameConfirmed
        self.onRenameDismissed = onRenameDismissed
        self.onDeleteRequested = onDeleteRequested
        self.onDeleteConfirmed = onDeleteConfirmed
        self.onDeleteDismissed = onDeleteDismissed
    }

    @SwiftUI.State private var scrollPosition: Int64?

    public var body: some View {
        stateBody
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
                            .accessibilityLabel(backButtonAccessibilityLabel)
                    },
                    trailingIcon: {
                        moreOptionsMenu
                    }
                ),
                alignment: .top
            )
            .edgesIgnoringSafeArea(.top)
            .alert(
                state.removeConfirmation?.title ?? "",
                isPresented: Binding(
                    get: { state.removeConfirmation != nil },
                    set: { isPresented in
                        if !isPresented { onRemoveDismissed() }
                    }
                )
            ) {
                if let confirmation = state.removeConfirmation {
                    Button(confirmation.confirmLabel, role: .destructive) {
                        onRemoveConfirmed()
                    }
                    Button(state.cancelLabel, role: .cancel) {
                        onRemoveDismissed()
                    }
                }
            } message: {
                Text(state.removeConfirmation?.message ?? "")
            }
            .alert(
                state.renameDialog?.title ?? "",
                isPresented: Binding(
                    get: { state.renameDialog != nil },
                    set: { isPresented in
                        if !isPresented { onRenameDismissed() }
                    }
                )
            ) {
                if let renameDialog = state.renameDialog {
                    TextField(
                        "",
                        text: Binding(
                            get: { state.renameDialog?.name ?? "" },
                            set: onRenameNameChanged
                        )
                    )
                    Button(renameDialog.saveLabel) {
                        onRenameConfirmed()
                    }
                    .disabled(!renameDialog.canSave || renameDialog.isSaving)
                    Button(state.cancelLabel, role: .cancel) {
                        onRenameDismissed()
                    }
                }
            }
            .alert(
                state.deleteConfirmation?.title ?? "",
                isPresented: Binding(
                    get: { state.deleteConfirmation != nil },
                    set: { isPresented in
                        if !isPresented { onDeleteDismissed() }
                    }
                )
            ) {
                if let deleteConfirmation = state.deleteConfirmation {
                    Button(deleteConfirmation.confirmLabel, role: .destructive) {
                        onDeleteConfirmed()
                    }
                    Button(state.cancelLabel, role: .cancel) {
                        onDeleteDismissed()
                    }
                }
            } message: {
                Text(state.deleteConfirmation?.message ?? "")
            }
    }

    private var moreOptionsMenu: some View {
        Menu {
            Button(action: onRenameRequested) {
                Label(state.renameLabel, systemImage: "pencil")
            }
            Button(role: .destructive, action: onDeleteRequested) {
                Label(state.deleteLabel, systemImage: "trash")
            }
        } label: {
            GlassMenuLabel(icon: "ellipsis")
        }
        .accessibilityLabel(moreOptionsAccessibilityLabel)
    }

    @ViewBuilder
    private var stateBody: some View {
        if state.isLoading {
            loadingPlaceholder
        } else if let errorMessage = state.errorMessage {
            EmptyStateView(
                systemName: "exclamationmark.triangle",
                title: errorMessage,
                buttonText: state.dismissErrorLabel,
                action: onDismissErrorMessage
            )
            .padding(.top, toolbarInset)
        } else if state.items.isEmpty {
            EmptyStateView(title: state.emptyMessage)
                .padding(.top, toolbarInset)
        } else {
            contentScrollView
        }
    }

    private var loadingPlaceholder: some View {
        ScrollView(.vertical, showsIndicators: false) {
            LazyVGrid(
                columns: ImageDimens.posterGridColumns(widthSizeClass, spacing: ImageDimens.gridItemSpacing),
                spacing: ImageDimens.gridItemSpacing
            ) {
                ForEach(0 ..< 12, id: \.self) { _ in
                    ShimmerView(cornerRadius: theme.shapes.medium)
                        .frame(width: posterWidth, height: posterHeight)
                }
            }
            .padding(.all, theme.spacing.xSmall)
        }
        .contentMargins(.top, toolbarInset + theme.spacing.medium)
    }

    private var contentScrollView: some View {
        ScrollView(.vertical, showsIndicators: false) {
            LazyVGrid(
                columns: ImageDimens.posterGridColumns(widthSizeClass, spacing: ImageDimens.gridItemSpacing),
                spacing: ImageDimens.gridItemSpacing
            ) {
                ForEach(state.items) { item in
                    PosterItemView(
                        title: item.title,
                        posterUrl: item.posterUrl,
                        posterWidth: posterWidth,
                        posterHeight: posterHeight
                    )
                    .aspectRatio(contentMode: .fill)
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                    .clipped()
                    .onTapGesture { onShowClicked(item.showId) }
                    .contextMenu {
                        Button(role: .destructive) {
                            onRemoveRequested(item.showId)
                        } label: {
                            Label(state.removeButtonLabel, systemImage: "trash")
                        }
                    }
                    .onAppear {
                        if let index = state.items.firstIndex(of: item) {
                            onItemAppear(index)
                            if index >= state.items.count - 6 {
                                onLoadMore()
                            }
                        }
                    }
                }
            }
            .scrollTargetLayout()
            .padding(.all, theme.spacing.xSmall)

            if state.isLoadingMore {
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

                    Button(action: onRetryLoadMore) {
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
            guard let newPosition, !state.isLoadingMore else { return }
            if let index = state.items.firstIndex(where: { $0.showId == newPosition }),
               index >= state.items.count - 6
            {
                onLoadMore()
            }
        }
        .contentMargins(.top, toolbarInset + theme.spacing.medium)
        .refreshable(enabled: state.canRefresh, action: onRefresh)
    }

    private var posterWidth: CGFloat {
        130
    }

    private var posterHeight: CGFloat {
        200
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}

private extension View {
    @ViewBuilder
    func refreshable(enabled: Bool, action: @escaping () -> Void) -> some View {
        if enabled {
            refreshable { action() }
        } else {
            self
        }
    }
}
