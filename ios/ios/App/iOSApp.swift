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

  @Environment(\.scenePhase)
  var scenePhase: ScenePhase

  var body: some Scene {
    WindowGroup {
      RootView(rootPresenter: appDelegate.presenterComponent.rootPresenter)
        .onChange(of: scenePhase) { newPhase in
          switch newPhase {
          case .background:
            appDelegate.lifecycle.stop()
          case .inactive:
            appDelegate.lifecycle.pause()
          case .active:
            appDelegate.lifecycle.resume()
          @unknown default:
            break
          }
        }
    }
  }
}
