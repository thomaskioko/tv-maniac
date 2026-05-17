import FirebaseRemoteConfig
import TvManiac

public class FirebaseRemoteConfigBridge: RemoteConfigBridge {
    private let remoteConfig: RemoteConfig
    private var listenerRegistration: ConfigUpdateListenerRegistration?

    public init() {
        remoteConfig = RemoteConfig.remoteConfig()
    }

    public func setMinimumFetchIntervalSeconds(seconds: Int64) {
        let settings = RemoteConfigSettings()
        settings.minimumFetchInterval = TimeInterval(seconds)
        remoteConfig.configSettings = settings
    }

    public func fetchAndActivate(onResult: @escaping (KotlinBoolean) -> Void) {
        remoteConfig.fetchAndActivate { status, _ in
            let success = status == .successFetchedFromRemote
                || status == .successUsingPreFetchedData
            DispatchQueue.main.async {
                onResult(KotlinBoolean(bool: success))
            }
        }
    }

    public func getBoolean(key: String) -> Bool {
        remoteConfig.configValue(forKey: key).boolValue
    }

    public func setDefaults(defaults: [String: KotlinBoolean]) {
        var nsDefaults: [String: NSObject] = [:]
        for (key, value) in defaults {
            nsDefaults[key] = NSNumber(value: value.boolValue)
        }
        remoteConfig.setDefaults(nsDefaults)
    }

    public func addOnConfigUpdateListener(onUpdate: @escaping () -> Void) {
        listenerRegistration?.remove()
        listenerRegistration = remoteConfig.addOnConfigUpdateListener { configUpdate, error in
            guard error == nil, configUpdate != nil else { return }
            self.remoteConfig.activate { _, _ in
                DispatchQueue.main.async {
                    onUpdate()
                }
            }
        }
    }
}
