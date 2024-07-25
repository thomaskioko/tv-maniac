import SwiftUI
import TvManiac

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    var body: some Scene {

        WindowGroup {
            RootView(rootComponent: appDelegate.presenterComponent.rootComponent)
                .environmentObject(NavigationModel())
        }
    }

}
