import SwiftUI
import SwiftUIComponents
import TvManiacKit

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    init() {
        TvManiacTypographyScheme.configureMoko()
    }

    @Environment(\.scenePhase)
    var scenePhase: ScenePhase

    @State private var authCoordinator: TraktAuthCoordinator?

    var body: some Scene {
        WindowGroup {
            RootNavigationView(
                rootPresenter: appDelegate.presenterComponent.rootPresenter,
                rootNavigator: appDelegate.presenterComponent.rootNavigator
            )
            .environmentObject(appDelegate)
            .onAppear {
                if authCoordinator == nil {
                    authCoordinator = AuthCoordinatorFactory.create(
                        authRepository: appDelegate.traktAuthRepository,
                        logger: appDelegate.logger
                    )
                }

                appDelegate.setupAuthBridge { [weak authCoordinator] in
                    authCoordinator?.initiateAuthorization()
                }
            }
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
