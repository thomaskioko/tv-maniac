import SwiftUI
import TvManiacKit

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    @Environment(\.scenePhase)
    var scenePhase: ScenePhase

    var body: some Scene {
        WindowGroup {
            RootNavigationView(
                rootPresenter: appDelegate.presenterComponent.rootPresenter,
                rootNavigator: appDelegate.presenterComponent.rootNavigator
            )
            .environmentObject(appDelegate)
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
