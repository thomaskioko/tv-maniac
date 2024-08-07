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
    
    private let component: SearchComponent
    
    @StateFlow private var uiState: SearchState
    @State private var query = String()
    
    init(component: SearchComponent){
        self.component = component
        _uiState = StateFlow(component.state)
    }
    
    var body: some View {
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
