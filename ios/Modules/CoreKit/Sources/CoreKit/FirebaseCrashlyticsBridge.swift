import FirebaseCrashlytics
import TvManiac

public class FirebaseCrashlyticsBridge: CrashReportingBridge {
    public init() {}

    public func setCollectionEnabled(enabled: Bool) {
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(enabled)
    }

    public func recordException(throwable: KotlinThrowable) {
        let error = NSError(
            domain: String(describing: type(of: throwable)),
            code: 0,
            userInfo: [NSLocalizedDescriptionKey: throwable.message ?? "Unknown error"]
        )
        Crashlytics.crashlytics().record(error: error)
    }

    public func recordException(throwable: KotlinThrowable, tag: String) {
        Crashlytics.crashlytics().setCustomValue(tag, forKey: "tag")
        recordException(throwable: throwable)
    }

    public func setCustomKey(key: String, value: String) {
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
    }

    public func setUserId(userId: String) {
        Crashlytics.crashlytics().setUserID(userId)
    }

    public func log(message: String) {
        Crashlytics.crashlytics().log(message)
    }
}
