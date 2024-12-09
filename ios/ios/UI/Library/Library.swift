import SwiftUI
import TvManiac

struct Library: View {
  private let presenter: LibraryPresenter

  init(presenter: LibraryPresenter) {
    self.presenter = presenter
  }

  var body: some View {
    Text("Library")
  }
}