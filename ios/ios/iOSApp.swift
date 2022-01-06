import SwiftUI
import TvManiac

@main
struct iOSApp: App {
	
   @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
	
	var body: some Scene {
		WindowGroup {
			HomeUIView()
		}
	}
}

class AppDelegate : NSObject, UIApplicationDelegate {
	func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
		KoinKt.doInitKoin()
		return true
	}
}
