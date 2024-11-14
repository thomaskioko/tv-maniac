import SwiftUI
import TvManiacKit

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    var body: some Scene {

        WindowGroup {
          RootView(rootPresenter: appDelegate.presenterComponent.rootPresenter)
                .environmentObject(NavigationModel())
        }
    }

}
