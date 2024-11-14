//
//  SearchView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacUI
import TvManiacKit

struct SearchView: View {
  private let presenter: SearchShowsPresenter
  @StateFlow private var uiState: SearchShowState
  @FocusState private var isSearchFocused: Bool
  @StateObject private var keyboard = KeyboardHeightManager()

  init(presenter: SearchShowsPresenter) {
    self.presenter = presenter
    _uiState = StateFlow(presenter.state)
  }

  var body: some View {
    VStack {
      searchBarView()
        .zIndex(1)

      ScrollView(.vertical, showsIndicators: false) {
        switch onEnum(of: uiState) {
        case .emptySearchState:
          CenteredFullScreenView {
            FullScreenView(
              systemName: "exclamationmark.magnifyingglass",
              message: "No results found. Try a different keyword!"
            )
          }
        case .errorSearchState(let state):
          FullScreenView(
            systemName: "exclamationmark.arrow.triangle.2.circlepath",
            message: state.errorMessage ?? "No results found. Try a different keyword!",
            buttonText: "Retry",
            action: { presenter.dispatch(action: ReloadShowContent()) }
          )
        case .searchResultAvailable(let state):
          searchResultsContent(state: state)
        case .showContentAvailable(let state):
          showContent(state: state)
        }
      }
      .simultaneousGesture(
        DragGesture().onChanged { _ in
          dismissKeyboard()
        }
      )
    }
    .navigationTitle("Search")
    .navigationBarTitleDisplayMode(.large)
    .background(Color.background)
    .environment(\.keyboardHeight, keyboard.keyboardHeight)
  }

  // MARK: - Bindings

  private var searchQueryBinding: Binding<String> {
    Binding(
      get: { uiState.query ?? "" },
      set: { newValue in
        let trimmedValue = newValue.trimmingCharacters(in: .whitespaces)
        if !trimmedValue.isEmpty {
          presenter.dispatch(action: QueryChanged(query: newValue))
        }
      }
    )
  }

  @ViewBuilder
  private func searchBarView() -> some View {
    HStack {
      Image(systemName: "magnifyingglass")
        .foregroundColor(.gray)

      TextField("Enter Show Title", text: searchQueryBinding)
        .textInputAutocapitalization(.never)
        .autocorrectionDisabled()
        .focused($isSearchFocused)
        .submitLabel(.search)
        .lineLimit(1)

      Button(
        action: { handleClearQuery() },
        label: {
          Image(systemName: "xmark.circle.fill")
            .foregroundColor(.gray)
        }
      )
    }
    .padding()
    .background(
      RoundedRectangle(cornerRadius: 10)
        .stroke(.gray)
        .background(Color.background)
    )
    .onTapGesture {
      isSearchFocused = true
    }
    .padding()
  }

  @ViewBuilder
  private func showContent(state: ShowContentAvailable) -> some View {
    LazyVStack(spacing: 16) {
      if state.isUpdating {
        CenteredFullScreenView {
          LoadingIndicatorView(animate: true)
            .frame(
              maxWidth: UIScreen.main.bounds.width,
              maxHeight: UIScreen.main.bounds.height,
              alignment: .center
            )
        }
      } else {
        ForEach(
          [
            ("Featured", state.featuredShows),
            ("Trending Today", state.trendingShows),
            ("Upcoming Today", state.upcomingShows)
          ], id: \.0
        ) { title, shows in
          if let shows = shows, !shows.isEmpty {
            showSection(title: title, shows: shows)
              .padding(.top, title != "Featured" ? 8 : 0)
          }
        }
      }
    }
  }

  @ViewBuilder
  private func showSection(title: String, shows: [ShowItem]) -> some View {
    HorizontalShowContentView(
      title: title,
      items: shows.map { $0.toSwift() },
      onClick: { id in
        presenter.dispatch(action: SearchShowClicked(id: id))
        dismissKeyboard()
      }
    )
  }

  @ViewBuilder
  private func searchResultsContent(state: SearchResultAvailable) -> some View {
    if state.isUpdating {
      CenteredFullScreenView {
        LoadingIndicatorView(animate: true)
          .frame(
            maxWidth: UIScreen.main.bounds.width,
            maxHeight: UIScreen.main.bounds.height,
            alignment: .center
          )
      }
    }

    if let shows = state.results, !shows.isEmpty {
      SearchResultListView(
        items: shows.map { $0.toSwift() },
        onClick: { id in
          presenter.dispatch(action: SearchShowClicked(id: id))
          dismissKeyboard()
        }
      )
    }
  }

  private func handleClearQuery() {
    if let query = uiState.query, !query.trimmingCharacters(in: .whitespaces).isEmpty {
      presenter.dispatch(action: ClearQuery())
    }
    dismissKeyboard()
  }

  private func dismissKeyboard() {
    withAnimation {
      isSearchFocused = false
    }
  }
}

private extension ShowItem {
  func toSwift() -> SwiftShow {
    .init(
      tmdbId: tmdbId,
      title: title,
      posterUrl: posterImageUrl,
      backdropUrl: nil,
      inLibrary: inLibrary
    )
  }

  func toSwift() -> SwiftSearchShow {
    .init(
      tmdbId: tmdbId,
      title: title,
      overview: overview,
      status: status,
      imageUrl: posterImageUrl,
      year: year,
      voteAverage: voteAverage?.doubleValue
    )
  }
}
