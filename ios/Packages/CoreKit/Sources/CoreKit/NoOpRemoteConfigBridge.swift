import TvManiac

/// Swift-side no-op fallback for `RemoteConfigBridge`. `AppDelegate` constructs this when
/// `GoogleService-Info.plist` is missing, so the Metro graph still receives a valid bridge.
public final class NoOpRemoteConfigBridge: RemoteConfigBridge {
    public init() {}

    public func setMinimumFetchIntervalSeconds(seconds _: Int64) {}

    public func fetchAndActivate(onResult: @escaping (KotlinBoolean) -> Void) {
        onResult(KotlinBoolean(bool: false))
    }

    public func getBoolean(key _: String) -> Bool {
        false
    }

    public func setDefaults(defaults _: [String: KotlinBoolean]) {}

    public func addOnConfigUpdateListener(onUpdate _: @escaping () -> Void) {}
}
