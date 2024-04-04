//
//  SearchView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct SearchView: View {
    
    private let presenter: SearchPresenter

    @ObservedObject
    private var uiState: StateFlow<SearchState>
    @State private var query = String()
    
    init(presenter: SearchPresenter){
        self.presenter = presenter
        self.uiState = StateFlow<SearchState>(presenter.state)
    }
    
    var body: some View {
        NavigationStack {
            VStack {
               
            }
            .navigationTitle("Search")
            .navigationBarTitleDisplayMode(.large)
            .background(Color.background)
            .searchable(text: $query)
            .task(id: query) {
                if query.isEmpty { return }
                if Task.isCancelled { return }
            }
        }
    }
}
