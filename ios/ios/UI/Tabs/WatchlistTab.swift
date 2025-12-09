import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct WatchlistTab: View {
    @Theme private var theme

    private let presenter: WatchlistPresenter
    @StateObject @KotlinStateFlow private var uiState: WatchlistState
    @State private var showListSelection = false
    @State private var isRotating = 0.0
    @Namespace private var animation

    init(presenter: WatchlistPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    private var searchQueryBinding: Binding<String> {
        Binding(
            get: { uiState.query },
            set: { newValue in
                let trimmedValue = newValue.trimmingCharacters(in: .whitespaces)
                if !trimmedValue.isEmpty {
                    presenter.dispatch(action: WatchlistQueryChanged(query: newValue))
                } else {
                    presenter.dispatch(action: ClearWatchlistQuery())
                }
            }
        )
    }

    var body: some View {
        ZStack {
            theme.colors.background
                .ignoresSafeArea()

            VStack {
                contentView
                    .padding(.top, theme.spacing.medium)
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .disableAutocorrection(true)
        .toolbar {
            let image = if uiState.isGridMode {
                "list.bullet"
            } else {
                "rectangle.grid.2x2"
            }
            ToolbarItem(placement: .navigationBarLeading) {
                HStack {
                    Button {
                        withAnimation {
                            presenter.dispatch(action: ChangeListStyleClicked())
                        }
                    } label: {
                        Label(String(\.label_watchlist_list_style), systemImage: image)
                            .labelStyle(.iconOnly)
                    }
                    .buttonBorderShape(.roundedRectangle(radius: theme.shapes.large))
                    .buttonStyle(.bordered)
                    .tint(theme.colors.accent)
                }
            }
            ToolbarItem(placement: .principal) {
                titleView
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                HStack {
                    filterButton
                }
            }
        }
        .searchable(
            text: searchQueryBinding,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: String(\.label_search_placeholder)
        )
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .toolbarBackground(theme.colors.surface, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
    }

    @ViewBuilder
    private var contentView: some View {
        if uiState.items.isEmpty {
            emptyView
        } else {
            watchlistContent(uiState)
        }
    }

    private var titleView: some View {
        HStack {
            Text(String(\.label_tab_watchlist))
                .textStyle(theme.typography.titleMedium)
                .fontWeight(.semibold)
                .lineLimit(1)
                .foregroundColor(theme.colors.onSurface)
            Button {
                withAnimation {
                    showListSelection.toggle()
                }
            } label: {
                Image(systemName: "chevron.down.circle.fill")
                    .fontWeight(.bold)
                    .textStyle(theme.typography.labelSmall)
                    .foregroundColor(theme.colors.onSurfaceVariant)
                    .rotationEffect(.degrees(isRotating))
                    .task(id: showListSelection) {
                        withAnimation(.easeInOut) {
                            if showListSelection {
                                isRotating = -180.0
                            } else {
                                isRotating = 0.0
                            }
                        }
                    }
            }
        }
    }

    private var filterButton: some View {
        Button {
            withAnimation {
                // TODO: Show Filter menu
            }
        } label: {
            Label(String(\.label_watchlist_sort_list), systemImage: "line.3.horizontal.decrease.circle")
                .labelStyle(.iconOnly)
        }
        .buttonBorderShape(.roundedRectangle(radius: theme.shapes.large))
        .buttonStyle(.bordered)
        .tint(theme.colors.accent)
    }

    @ViewBuilder
    private func watchlistContent(_ content: WatchlistState) -> some View {
        if !content.items.isEmpty {
            ScrollView(showsIndicators: false) {
                if !uiState.isGridMode {
                    listViewContent(content)
                } else {
                    gridViewContent(content)
                }
            }
            .animation(.spring(response: 0.4, dampingFraction: 0.8), value: uiState.isGridMode)
        } else {
            emptyView
        }
    }

    @ViewBuilder
    private func listViewContent(_ state: WatchlistState) -> some View {
        LazyVStack(spacing: theme.spacing.xSmall) {
            ForEach(state.items, id: \.tmdbId) { item in
                WatchlistListItem(item: item, namespace: animation)
                    .onTapGesture {
                        presenter.dispatch(action: WatchlistShowClicked(id: item.tmdbId))
                    }
                    .transition(
                        .asymmetric(
                            insertion: .scale(scale: 0.9).combined(with: .opacity),
                            removal: .scale(scale: 1.1).combined(with: .opacity)
                        )
                    )
            }
        }
        .padding(.horizontal)
        .transition(
            .asymmetric(
                insertion: .scale(scale: 0.8).combined(with: .opacity),
                removal: .scale(scale: 1.2).combined(with: .opacity)
            )
        )
    }

    @ViewBuilder
    private func gridViewContent(_ state: WatchlistState) -> some View {
        LazyVGrid(columns: WatchlistConstants.columns, spacing: WatchlistConstants.spacing) {
            ForEach(state.items, id: \.tmdbId) { item in
                ZStack(alignment: .bottom) {
                    PosterItemView(
                        title: item.title,
                        posterUrl: item.posterImageUrl
                    )

                    ProgressView(value: item.watchProgress, total: 1)
                        .progressViewStyle(RoundedRectProgressViewStyle())
                        .offset(y: 2)
                }
                .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                .clipped()
                .matchedGeometryEffect(id: item.tmdbId, in: animation)
                .onTapGesture {
                    presenter.dispatch(action: WatchlistShowClicked(id: item.tmdbId))
                }
            }
        }
        .padding(.horizontal)
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

public enum WatchlistConstants {
    static let spacing: CGFloat = 4
    static let columns: [GridItem] = [
        GridItem(.adaptive(minimum: 100), spacing: spacing),
    ]
}
