import SwiftUI
import TvManiac

struct Search: View {
  private let presenter: SearchShowsPresenter
  
  init(presenter: SearchShowsPresenter) {
    self.presenter = presenter
  }
  
  var body: some View {
    Text("Search")
  }
} 
