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

struct SearchView: View {
  private let component: SearchShowsComponent
  @StateFlow private var uiState: SearchShowState
  @FocusState private var isSearchFocused: Bool

  // Remove @State query since we're using uiState.query directly

  init(component: SearchShowsComponent) {
    self.component = component
    _uiState = StateFlow(component.state)
  }

  var body: some View {
    VStack {
      searchBarView()
        .zIndex(1)

      ScrollView {
        switch onEnum(of: uiState) {
        case .emptySearchState:
          FullScreenView(
            systemName: "exclamationmark.arrow.triangle.2.circlepath",
            buttonText: "Retry",
            action: {}
          )
        case .errorSearchState(let state):
          FullScreenView(
            systemName: "exclamationmark.magnifyingglass",
            message: state.errorMessage ?? "No results found. Try a different keyword!",
            buttonText: "Retry",
            action: { component.dispatch(action: ReloadShowContent()) }
          )
        case .searchResultAvailable(let state):
          searchResultsContent(state: state)
        case .showContentAvailable(let state):
          showContent(state: state)
        }
      }
      // Add gesture recognizer at ScrollView level instead of child views
      .simultaneousGesture(
        DragGesture().onChanged { _ in
          dismissKeyboard()
        }
      )
    }
    .navigationTitle("Search")
    .navigationBarTitleDisplayMode(.large)
    .background(Color.background)
  }

  @ViewBuilder
  private func searchBarView() -> some View {
    HStack {
      Image(systemName: "magnifyingglass")
        .foregroundColor(.gray)

      TextField("Enter Show Title", text: Binding(
        get: { uiState.query ?? "" },
        set: { newValue in
          if !newValue.trimmingCharacters(in: .whitespaces).isEmpty {
            component.dispatch(action: QueryChanged(query: newValue))
          }
        }
      ))
      .textInputAutocapitalization(.never)
      .autocorrectionDisabled()
      .focused($isSearchFocused)
      .submitLabel(.search)
      .lineLimit(1)

      Button(
        action: {
          if let query = uiState.query, !query.trimmingCharacters(in: .whitespaces).isEmpty {
            component.dispatch(action: ClearQuery())
          }
          dismissKeyboard()
        },
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
    VStack(spacing: 16) {
      ForEach([
        ("Featured", state.featuredShows),
        ("Trending Today", state.trendingShows),
        ("Upcoming Today", state.upcomingShows)
      ], id: \.0) { title, shows in
        if let shows = shows, !shows.isEmpty {
          showSection(title: title, shows: shows)
            .padding(.top, title != "Featured" ? 8 : 0)
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
        component.dispatch(action: SearchShowClicked(id: id))
        dismissKeyboard()
      }
    )
  }

  @ViewBuilder
  private func searchResultsContent(state: SearchResultAvailable) -> some View {
    if let shows = state.results, !shows.isEmpty {
      SearchResultListView(
        items: shows.map { $0.toSwift() },
        onClick: { id in
          component.dispatch(action: SearchShowClicked(id: id))
          dismissKeyboard()
        }
      )
    }
  }

  // Centralize keyboard dismissal
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
