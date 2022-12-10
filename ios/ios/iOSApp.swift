import SwiftUI
import TvManiac

@main
struct iOSApp: App {

	@UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
	@ObservedObject var viewModel: SettingsViewModel = SettingsViewModel(settingsState: SettingsContent.companion.EMPTY)
	@Environment(\.colorScheme) var systemColorScheme: ColorScheme

	var body: some Scene {
		WindowGroup {
			HomeUIView()
					.preferredColorScheme(colorScheme)
					.onAppear {
						viewModel.startStateMachine()
					}.environmentObject(viewModel)
		}
	}

	var colorScheme: ColorScheme? {
		withAnimation {
			switch viewModel.appTheme {
			case .Dark:
				return .dark
			case .Light:
				return .light
			default:
				return .light
			}
		}
	}
}

class AppDelegate : NSObject, UIApplicationDelegate {
	func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
		KoinApplication.start()
		return true
	}
}
