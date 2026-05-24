import DesignSystem
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

    @State private var componentHolder: ComponentHolder<IosViewPresenterGraph>?
    @State private var authCoordinator: TraktAuthCoordinator?
    @State private var toastManager = ToastManager()
    private let screenRegistry = ScreenRegistryBootstrap.makeRegistry()

    init() {
        TvManiacTypographyScheme.configureMoko()
    }

    @State private var dragOffsetX: CGFloat = 0
    @State private var dragOffsetY: CGFloat = 0

    var body: some Scene {
        WindowGroup {
            if let holder = componentHolder {
                RootToastForwarder(
                    rootPresenter: holder.component.rootPresenter,
                    navigator: holder.component.navigator,
                    registry: screenRegistry
                )
                .environmentObject(appDelegate)
                .environment(toastManager)
                .overlay(alignment: .top) {
                    if let toast = toastManager.toast {
                        ToastView(
                            type: toast.type,
                            title: toast.title,
                            message: toast.message,
                            loading: toast.loading,
                            onCancelTapped: {
                                if toast.persistent {
                                    holder.component.rootPresenter.dismissSyncStatus()
                                }
                                toastManager.dismiss()
                            }
                        )
                        .offset(x: dragOffsetX, y: dragOffsetY)
                        .highPriorityGesture(
                            DragGesture()
                                .onChanged { value in
                                    dragOffsetX = value.translation.width
                                    dragOffsetY = min(0, value.translation.height)
                                }
                                .onEnded { value in
                                    if value.translation.height < -50 || abs(value.translation.width) > 50 {
                                        if toast.persistent {
                                            holder.component.rootPresenter.dismissSyncStatus()
                                        }
                                        toastManager.dismiss()
                                        dragOffsetX = 0
                                        dragOffsetY = 0
                                    } else {
                                        withAnimation(.spring()) {
                                            dragOffsetX = 0
                                            dragOffsetY = 0
                                        }
                                    }
                                }
                        )
                        .transition(.move(edge: .top).combined(with: .opacity))
                        .padding(.top, 8)
                        .onChange(of: toastManager.toast) { _, newValue in
                            if newValue == nil {
                                dragOffsetX = 0
                                dragOffsetY = 0
                            }
                        }
                    }
                }
                .animation(.spring(), value: toastManager.toast)
                .onAppear {
                    setupAuthCoordinator()
                    appDelegate.configureNotificationDelegate(rootPresenter: holder.component.rootPresenter)
                }
                .onChange(of: scenePhase) { _, newPhase in
                    handleScenePhaseChange(newPhase, lifecycle: holder.lifecycle)
                }
            } else {
                Color.clear.onAppear {
                    componentHolder = ComponentHolder { context in
                        appDelegate.appGraph.viewPresenterGraphFactory.createGraph(
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
                traktConfig: appDelegate.traktConfig,
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
            LifecycleRegistryExtKt.stop(lifecycle)
        case .inactive:
            LifecycleRegistryExtKt.pause(lifecycle)
        case .active:
            LifecycleRegistryExtKt.resume(lifecycle)
        @unknown default:
            break
        }
    }
}

private struct RootToastForwarder: View {
    let rootPresenter: RootPresenter
    let navigator: Navigator
    let registry: ScreenRegistry

    @StateValue private var toast: ToastState
    @Environment(ToastManager.self) private var toastManager

    init(rootPresenter: RootPresenter, navigator: Navigator, registry: ScreenRegistry) {
        self.rootPresenter = rootPresenter
        self.navigator = navigator
        self.registry = registry
        _toast = .init(rootPresenter.toastStateValue)
    }

    var body: some View {
        RootNavigationView(rootPresenter: rootPresenter, navigator: navigator, registry: registry)
            .onAppear { forward(toast) }
            .onChange(of: toast) { _, newState in
                forward(newState)
            }
    }

    private func forward(_ state: ToastState) {
        guard let message = state.message else {
            toastManager.dismiss()
            return
        }
        toastManager.show(
            Toast(
                type: state.type == .error ? .error : .info,
                message: message,
                persistent: state.persistent,
                loading: state.type == .status
            )
        )
    }
}
