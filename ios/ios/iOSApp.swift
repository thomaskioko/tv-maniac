import SwiftUI
import TvManiac

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate
    
    @Environment(\.scenePhase) var scenePhase: ScenePhase
    
    var rootHolder: RootHolder { appDelegate.rootHolder }
    
    var body: some Scene {
        
        WindowGroup {
            RootView(navigator: appDelegate.presenterComponent.navigator)
                .onChange(of: scenePhase) { newPhase in
                    switch newPhase {
                    case .background: LifecycleRegistryExtKt.stop(rootHolder.lifecycle)
                    case .inactive: LifecycleRegistryExtKt.pause(rootHolder.lifecycle)
                    case .active: LifecycleRegistryExtKt.resume(rootHolder.lifecycle)
                    @unknown default: break
                    }
                }
        }
    }

}
