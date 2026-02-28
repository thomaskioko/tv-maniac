import CoreKit
import FirebaseCore
import SwiftUI
import TvManiac
import UIKit
import UserNotifications

public class AppDelegate: NSObject, UIApplicationDelegate, ObservableObject {
    public lazy var appComponent = IosApplicationComponent.companion.create()

    public lazy var traktAuthRepository = appComponent.traktAuthRepository
    public lazy var logger = appComponent.logger
    public lazy var traktAuthManager = appComponent.traktAuthManager

    public private(set) var notificationDelegate: NotificationDelegate?

    override public init() {
        super.init()
        FirebaseApp.configure()
        CrashReportingBridgeHolder.shared.bridge = FirebaseCrashlyticsBridge()
        ImageCacheManager.configure()
        // Force IosTaskScheduler construction so BGTask handlers are registered
        // synchronously during app launch — Apple silently discards late registrations.
        _ = appComponent.backgroundTaskScheduler
        appComponent.initializers.initialize()
        setupNotifications()
        setupNotificationDelegate()

        let logBridge = KmpLoggerBridge(appComponent.logger)
        MemoryMonitor.shared.setLogger(logBridge)
        DefaultDiagnosticLogger.shared.setLogger(logBridge)
        MemoryMonitor.shared.setDiagnosticLogger(DefaultDiagnosticLogger.shared)
        MemoryMonitor.shared.logMemoryState(event: "AppDelegate.init")
    }

    public func setupAuthBridge(authCallback: @escaping () -> Void) {
        traktAuthManager.setAuthCallback(callback: authCallback)
    }

    private func setupNotificationDelegate() {
        notificationDelegate = NotificationDelegate()
        UNUserNotificationCenter.current().delegate = notificationDelegate
    }

    public func configureNotificationDelegate(rootPresenter: RootPresenter) {
        notificationDelegate?.setRootPresenter(rootPresenter)
    }

    private func setupNotifications() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleMemoryWarning),
            name: UIApplication.didReceiveMemoryWarningNotification,
            object: nil
        )

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(applicationDidEnterBackground),
            name: UIApplication.didEnterBackgroundNotification,
            object: nil
        )

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(applicationWillEnterForeground),
            name: UIApplication.willEnterForegroundNotification,
            object: nil
        )
    }

    @objc private func handleMemoryWarning() {
        let level = MemoryMonitor.shared.recordMemoryWarning()
        logger.debug(
            message: "[Memory] Warning #\(level) — \(SystemMemory.memoryUsageDescription)"
        )
        ImageCacheManager.handleMemoryWarning(escalationLevel: level)
    }

    @objc private func applicationDidEnterBackground() {
        MemoryMonitor.shared.logMemoryState(event: "didEnterBackground")
        ImageCacheManager.clearMemoryCache()
        MemoryMonitor.shared.stop()
        DefaultDiagnosticLogger.shared.logBreadcrumb(
            category: "lifecycle",
            message: "App entered background — caches cleared"
        )
        logger.debug(message: "[Memory] Background — cleared memory caches")

        appComponent.backgroundTaskScheduler.rescheduleBackgroundTask()
    }

    @objc private func applicationWillEnterForeground() {
        MemoryMonitor.shared.resetWarningEscalation()
        MemoryMonitor.shared.start()
        MemoryMonitor.shared.logMemoryState(event: "willEnterForeground")
        DefaultDiagnosticLogger.shared.logBreadcrumb(
            category: "lifecycle",
            message: "App entering foreground"
        )
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}
