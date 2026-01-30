import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    @Environment(\.scenePhase)
    var scenePhase: ScenePhase

    @State private var componentHolder: ComponentHolder<IosViewPresenterComponent>?
    @State private var authCoordinator: TraktAuthCoordinator?

    init() {
        TvManiacTypographyScheme.configureMoko()
    }

    var body: some Scene {
        WindowGroup {
            if let holder = componentHolder {
                RootNavigationView(
                    rootPresenter: holder.component.rootPresenter,
                    rootNavigator: holder.component.rootNavigator
                )
                .environmentObject(appDelegate)
                .onAppear {
                    setupAuthCoordinator()
                }
                .onChange(of: scenePhase) { newPhase in
                    handleScenePhaseChange(newPhase, lifecycle: holder.lifecycle)
                }
            } else {
                Color.clear.onAppear {
                    componentHolder = ComponentHolder { context in
                        appDelegate.appComponent.componentFactory.createComponent(
                            componentContext: context
                        )
                    }
                }
            }
        }
    }

    private func setupAuthCoordinator() {
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

    private func handleScenePhaseChange(_ phase: ScenePhase, lifecycle: LifecycleRegistry) {
        switch phase {
        case .background:
            lifecycle.stop()
            ImageSystemManager.shared.handleBackgroundCleanup()
        case .inactive:
            lifecycle.pause()
        case .active:
            lifecycle.resume()
        @unknown default:
            break
        }
    }
}
