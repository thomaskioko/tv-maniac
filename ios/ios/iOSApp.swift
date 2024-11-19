import SwiftUI
import SwiftUIComponents
import TvManiacKit

@main
struct iOSApp: App {
  init() {
    FontRegistration.register()
  }

  @UIApplicationDelegateAdaptor(AppDelegate.self)
  var appDelegate: AppDelegate

  var body: some Scene {
    WindowGroup {
      RootView(rootPresenter: appDelegate.presenterComponent.rootPresenter)
        .environmentObject(NavigationModel())
    }
  }
}
