import FirebaseCrashlytics
import TvManiac

public final class FirebaseCrashlyticsConfiguration: CrashlyticsConfiguration {
    public let isConfigured: Bool

    public init(isConfigured: Bool) {
        self.isConfigured = isConfigured
    }

    public func setCollectionEnabled(enabled: Bool) {
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(enabled)
    }
}
