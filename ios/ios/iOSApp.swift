import SwiftUI
import TvManiac

@main
struct iOSApp: App {

    @StateObject var viewModel: SettingsViewModel = SettingsViewModel()
	@Environment(\.colorScheme) var systemColorScheme: ColorScheme

	var body: some Scene {
		WindowGroup {
			HomeUIView()
				.preferredColorScheme(colorScheme)
				.onAppear { viewModel.startStateMachine() }
				.environmentObject(viewModel)
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
