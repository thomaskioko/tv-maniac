import FirebaseCrashlytics
import TvManiac

public final class FirebaseCrashlyticsCollection: CrashlyticsCollection {
    public init() {}

    public func setEnabled(enabled: Bool) {
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(enabled)
    }
}
