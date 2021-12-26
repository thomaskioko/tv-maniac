import SwiftUI
import TvManiac

@main
struct iOSApp: App {
	
	private let networkModule = NetworkModule()
	private let databaseModule = DatabaseModule()
	
	var body: some Scene {
		WindowGroup {
			HomeUIView(
				networkModule: networkModule,
				databaseModule: databaseModule
			)
		}
	}
}
