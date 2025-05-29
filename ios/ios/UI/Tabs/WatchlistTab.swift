import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct WatchlistTab: View {
    private let presenter: WatchlistPresenter
    @StateObject @KotlinStateFlow private var uiState: WatchlistState
    @State private var showListSelection = false
    @State private var isRotating = 0.0
    @State private var changeListStyle = false // TODO: Get from uiState
    @Namespace private var animation

    init(presenter: WatchlistPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    private var searchQueryBinding: Binding<String> {
        Binding(
            get: { uiState.query ?? "" },
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
            Color.background
                .ignoresSafeArea()

            VStack {
                contentView
                    .padding(.top, 16)
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .disableAutocorrection(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                HStack {
                    Button(String(\.label_watchlist_list_style),
                           systemImage: changeListStyle ? "list.dash" : "square.grid.2x2") {
                        withAnimation {
                            changeListStyle.toggle()
                            presenter.dispatch(action: ChangeListStyleClicked())
                        }
                    }
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
            prompt: String(\.label_watchlist_search_hint)
        )
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .toolbarBackground(.ultraThinMaterial, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
    }

    @ViewBuilder
    private var contentView: some View {
        switch onEnum(of: uiState) {
        case .loadingShows:
            CenteredFullScreenView {
                LoadingIndicatorView()
            }
        case let .watchlistContent(state):
            watchlistContent(state)
        case let .emptyWatchlist(state):
            CenteredFullScreenView {
                FullScreenView(
                    systemName: "exclamationmark.magnifyingglass",
                    message: state.message ?? String(\.label_search_empty_results)
                )
                    .frame(maxWidth: .infinity)
            }
        }
    }

    private var titleView: some View {
        HStack {
            Text(String(\.label_tab_watchlist))
                .fontWeight(Font.Weight.semibold)
                .lineLimit(1)
                .foregroundColor(.secondary)
            Button {
                withAnimation {
                    showListSelection.toggle()
                }
            } label: {
                Image(systemName: "chevron.down.circle.fill")
                    .fontWeight(.bold)
                    .font(.caption)
                    .foregroundColor(.secondary)
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
        .buttonBorderShape(.roundedRectangle(radius: 16))
        .buttonStyle(.bordered)
    }

    @ViewBuilder
    private func watchlistContent(_ content: WatchlistContent) -> some View {
        if !content.list.isEmpty {
            ScrollView(showsIndicators: false) {
                if changeListStyle {
                    listViewContent(content)
                } else {
                    gridViewContent(content)
                }
            }
            .animation(.spring(response: 0.4, dampingFraction: 0.8), value: changeListStyle)
        } else {
            empty
        }
    }

    @ViewBuilder
    private func listViewContent(_ content: WatchlistContent) -> some View {
        LazyVStack(spacing: 8) {
            ForEach(content.list, id: \.tmdbId) { item in
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
    private func gridViewContent(_ content: WatchlistContent) -> some View {
        LazyVGrid(columns: WatchlistConstants.columns, spacing: WatchlistConstants.spacing) {
            ForEach(content.list, id: \.tmdbId) { item in
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
    private var empty: some View {
        let subtitle = uiState.query?.isEmpty ?? true ? "Add shows to keep track of them" : "Try a different keyword!"
        if let message = uiState.query?
            .isEmpty ?? true ? "Your watchlist is empty." : "No results found for '\(uiState.query ?? "")'." {
            CenteredFullScreenView {
                FullScreenView(
                    systemName: "magnifyingglass",
                    message: message,
                    subtitle: subtitle,
                    color: Color.secondary
                )
                    .frame(maxWidth: .infinity)
            }
        }
    }
}

public enum WatchlistConstants {
    static let spacing: CGFloat = 4
    static let columns: [GridItem] = [
        GridItem(.adaptive(minimum: 100), spacing: spacing),
    ]
}
