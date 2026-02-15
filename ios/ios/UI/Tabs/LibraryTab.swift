import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct LibraryTab: View {
    @Theme private var theme

    private let presenter: LibraryPresenter
    @StateObject @KotlinStateFlow private var uiState: LibraryState
    @State private var showSortOptions = false
    @FocusState private var isSearchFocused: Bool
    @Namespace private var animation

    init(presenter: LibraryPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    private var searchQueryBinding: Binding<String> {
        BindingFactories.searchQuery(
            get: { uiState.query },
            onChanged: { presenter.dispatch(action: LibraryQueryChanged(query: $0)) },
            onCleared: { presenter.dispatch(action: ClearLibraryQuery()) }
        )
    }

    var body: some View {
        ZStack {
            theme.colors.background
                .ignoresSafeArea()

            VStack {
                contentView
            }
            .padding(.top, DimensionConstants.toolbarInset)
        }
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .disableAutocorrection(true)
        .overlay(
            Group {
                if uiState.isSearchActive {
                    searchBarOverlay
                } else {
                    libraryToolbar
                }
            },
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
        .animation(.spring(response: 0.3, dampingFraction: 0.8), value: uiState.isSearchActive)
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .sheet(isPresented: $showSortOptions) {
            SortOptionsSheet(
                state: uiState,
                onSortOptionSelected: { sortOption in
                    presenter.dispatch(action: ChangeSortOption(sortOption: sortOption))
                },
                onGenreToggle: { genre in
                    presenter.dispatch(action: ToggleGenreFilter(genre: genre))
                },
                onStatusToggle: { status in
                    presenter.dispatch(action: ToggleStatusFilter(status: status))
                },
                onClearFilters: {
                    presenter.dispatch(action: ClearFilters())
                },
                onApplyFilters: {
                    showSortOptions = false
                }
            )
            .presentationDetents([.large])
        }
    }

    @ViewBuilder
    private var contentView: some View {
        if uiState.showLoading {
            CenteredFullScreenView {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                    .scaleEffect(1.5)
            }
        } else if uiState.isEmpty {
            emptyView
        } else if uiState.isGridMode {
            gridContent
        } else {
            listContent
        }
    }

    private var libraryToolbar: some View {
        let image = uiState.isGridMode ? "list.bullet" : "rectangle.grid.2x2"
        return GlassToolbar(
            title: String(\.label_library_title),
            opacity: 1.0,
            isLoading: uiState.isRefreshing,
            leadingIcon: {
                GlassButton(icon: image) {
                    withAnimation {
                        presenter.dispatch(action: ChangeListStyleClicked(isGridMode: uiState.isGridMode))
                    }
                }
            },
            trailingIcon: {
                HStack(spacing: theme.spacing.xSmall) {
                    GlassButton(icon: "magnifyingglass") {
                        withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
                            presenter.dispatch(action: ToggleSearchActive())
                            isSearchFocused = true
                        }
                    }
                    GlassButton(icon: "line.3.horizontal.decrease.circle") {
                        showSortOptions = true
                    }
                }
            }
        )
    }

    private var searchBarOverlay: some View {
        let topPadding = DimensionConstants.safeAreaTop
        return ZStack(alignment: .top) {
            theme.colors.surface
                .frame(height: DimensionConstants.toolbarHeight + topPadding)
                .ignoresSafeArea()

            expandedSearchBar
                .padding(.horizontal, theme.spacing.medium)
                .padding(.top, topPadding + theme.spacing.xSmall)
        }
        .frame(maxWidth: .infinity)
    }

    private var expandedSearchBar: some View {
        HStack(spacing: theme.spacing.small) {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(theme.colors.onSurfaceVariant)

                TextField(String(\.label_search_placeholder), text: searchQueryBinding)
                    .textStyle(theme.typography.bodyMedium)
                    .focused($isSearchFocused)
                    .submitLabel(.search)

                Button {
                    withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
                        if !uiState.query.isEmpty {
                            presenter.dispatch(action: ClearLibraryQuery())
                        } else {
                            presenter.dispatch(action: ToggleSearchActive())
                            isSearchFocused = false
                        }
                    }
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }
            .padding(.horizontal, theme.spacing.small)
            .padding(.vertical, 6)
            .background(theme.colors.surfaceVariant.opacity(0.5))
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
        }
        .frame(maxWidth: .infinity)
    }

    @ViewBuilder
    private var gridContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVGrid(columns: LibraryConstants.columns, spacing: LibraryConstants.spacing) {
                ForEach(Array(uiState.items), id: \.traktId) { item in
                    PosterItemView(
                        title: item.title,
                        posterUrl: item.posterImageUrl
                    )
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                    .clipped()
                    .matchedGeometryEffect(id: item.traktId, in: animation)
                    .onTapGesture {
                        presenter.dispatch(action: LibraryShowClicked(traktId: item.traktId))
                    }
                }
            }
            .padding(.horizontal, theme.spacing.xSmall)
            .padding(.top, theme.spacing.small)
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: uiState.isGridMode)
    }

    @ViewBuilder
    private var listContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: theme.spacing.small) {
                ForEach(Array(uiState.items), id: \.traktId) { item in
                    LibraryListItemView(
                        item: item.toSwift(),
                        onItemClicked: {
                            presenter.dispatch(action: LibraryShowClicked(traktId: item.traktId))
                        }
                    )
                }
            }
            .padding(.horizontal, theme.spacing.xSmall)
            .padding(.top, theme.spacing.small)
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: uiState.isGridMode)
    }

    @ViewBuilder
    private var emptyView: some View {
        let subtitle = uiState.query.isEmpty ? nil : String(\.label_watchlist_empty_result, parameter: uiState.query)

        CenteredFullScreenView {
            FullScreenView(
                systemName: "tray",
                message: String(\.generic_empty_content),
                subtitle: subtitle,
                color: theme.colors.onSurfaceVariant
            )
            .frame(maxWidth: .infinity)
        }
    }
}

public enum LibraryConstants {
    static let spacing: CGFloat = 4
    static let columns: [GridItem] = [
        GridItem(.adaptive(minimum: 100), spacing: spacing),
    ]
}

private enum DimensionConstants {
    static let toolbarHeight: CGFloat = 44
    static var safeAreaTop: CGFloat {
        (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
    }

    static var toolbarInset: CGFloat {
        toolbarHeight + safeAreaTop
    }
}
