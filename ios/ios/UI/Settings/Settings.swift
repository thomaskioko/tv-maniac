import SwiftUI
import TvManiac

struct Settings: View {
  private let presenter: SettingsPresenter
  
  init(presenter: SettingsPresenter) {
    self.presenter = presenter
  }
  
  var body: some View {
    Text("Settings")
  }
}
