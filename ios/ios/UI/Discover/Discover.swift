import SwiftUI
import TvManiac

struct Discover: View {
  private let presenter: DiscoverShowsPresenter
  
  init(presenter: DiscoverShowsPresenter) {
    self.presenter = presenter
  }
  
  var body: some View {
    Text("Discover")
  }
}
