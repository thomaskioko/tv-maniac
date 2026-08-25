import Foundation

public enum WidgetContainer {
    public static let snapshotFileName = "widget-snapshot.json"

    public static let postersFolderName = "widget-posters"

    public static func directoryURL(bundle: Bundle = .main) -> URL? {
        guard let identifier = bundle.object(forInfoDictionaryKey: "AppGroupIdentifier") as? String else {
            return nil
        }
        return FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: identifier)
    }

    public static func snapshotURL(bundle: Bundle = .main) -> URL? {
        directoryURL(bundle: bundle)?.appendingPathComponent(snapshotFileName)
    }

    public static func postersURL(bundle: Bundle = .main) -> URL? {
        directoryURL(bundle: bundle)?.appendingPathComponent(postersFolderName, isDirectory: true)
    }
}
