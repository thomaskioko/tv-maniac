import SwiftUI
import TvManiac

struct Search: View {
   private let presenter: SearchShowsPresenter
    
    init(presenter: SearchShowsPresenter) {
        self.presenter = presenter
    }
    
    var body: some View {
      VStack {
        Text("Search")
      }
      .navigationTitle("Search")
      .navigationBarTitleDisplayMode(.large)
    }
} 
