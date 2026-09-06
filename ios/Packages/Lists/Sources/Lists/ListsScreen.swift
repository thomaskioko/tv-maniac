import Components
import DesignSystem
import SwiftUI

public struct ListsScreen: View {
    @Environment(\.appTheme) private var theme

    private let state: State
    private let backButtonAccessibilityLabel: String
    private let onBack: () -> Void
    private let onListClicked: (Int64) -> Void

    public init(
        state: State,
        backButtonAccessibilityLabel: String = "",
        onBack: @escaping () -> Void = {},
        onListClicked: @escaping (Int64) -> Void = { _ in }
    ) {
        self.state = state
        self.backButtonAccessibilityLabel = backButtonAccessibilityLabel
        self.onBack = onBack
        self.onListClicked = onListClicked
    }

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
                    }
                ),
                alignment: .top
            )
            .edgesIgnoringSafeArea(.top)
    }

    @ViewBuilder
    private var stateBody: some View {
        if state.isLoading {
            loadingPlaceholder
        } else if let errorMessage = state.errorMessage {
            EmptyStateView(systemName: "exclamationmark.triangle", title: errorMessage)
                .padding(.top, toolbarInset)
        } else if state.lists.isEmpty {
            EmptyStateView(title: state.emptyMessage)
                .padding(.top, toolbarInset)
        } else {
            contentScrollView
        }
    }

    private var loadingPlaceholder: some View {
        ScrollView(showsIndicators: false) {
            LazyVGrid(columns: gridColumns, spacing: theme.spacing.small) {
                ForEach(0 ..< 4, id: \.self) { _ in
                    ShimmerView(cornerRadius: theme.shapes.large)
                        .frame(height: ListCollageCard.cardSize.height)
                }
            }
            .padding(.horizontal, theme.spacing.medium)
        }
        .contentMargins(.top, toolbarInset + theme.spacing.small)
    }

    private var contentScrollView: some View {
        ScrollView(showsIndicators: false) {
            LazyVGrid(columns: gridColumns, spacing: theme.spacing.small) {
                ForEach(state.lists) { list in
                    ListCollageCard(list: list, fillsAvailableWidth: true, onClick: { onListClicked(list.id) })
                }
            }
            .padding(.horizontal, theme.spacing.medium)
            .padding(.bottom, theme.spacing.large)
        }
        .contentMargins(.top, toolbarInset + theme.spacing.small)
    }

    private var gridColumns: [GridItem] {
        [
            GridItem(.flexible(), spacing: theme.spacing.small),
            GridItem(.flexible(), spacing: theme.spacing.small),
        ]
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}
