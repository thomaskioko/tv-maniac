import Foundation
import Nuke

public enum ImageConfiguration {
    private static var isConfigured = false

    public static func configure() {
        guard !isConfigured else { return }
        isConfigured = true
    }

    public static func clearCache() {
        ImagePipeline.shared.cache.removeAll()
    }

    public static func transformURL(_ urlString: String) -> URL? {
        guard !urlString.isEmpty else { return nil }
        var transformed = urlString
        if urlString.contains("image.tmdb.org") {
            let tmdbSize = currentTmdbSize()
            transformed = urlString.replacingOccurrences(of: "/original/", with: tmdbSize)
            if transformed == urlString {
                transformed = transformed.replacingOccurrences(
                    of: #"/w\d+/"#,
                    with: tmdbSize,
                    options: .regularExpression
                )
            }
        }
        return URL(string: transformed)
    }

    private static func currentTmdbSize() -> String {
        let qualityString = UserDefaults.standard.string(forKey: "image.quality") ?? "MEDIUM"
        switch qualityString {
        case "HIGH": return "/original/"
        case "LOW": return "/w185/"
        default: return "/w500/"
        }
    }
}
