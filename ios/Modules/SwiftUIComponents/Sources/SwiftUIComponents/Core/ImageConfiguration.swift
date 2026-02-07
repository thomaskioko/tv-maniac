import Foundation
import Nuke
import UIKit

public enum TmdbImageType {
    case poster
    case backdrop
    case profile
}

public enum ImageConfiguration {
    private static var isConfigured = false

    public static func configure() {
        guard !isConfigured else { return }
        isConfigured = true

        let config = ImagePipeline.Configuration.withDataCache(name: "com.thomaskioko.tvmaniac.images")
        ImagePipeline.shared = ImagePipeline(configuration: config)
    }

    public static func clearCache() {
        ImagePipeline.shared.cache.removeAll()
    }

    public static func transformURL(_ urlString: String, imageType: TmdbImageType? = nil) -> URL? {
        guard !urlString.isEmpty else { return nil }
        var transformed = urlString
        if urlString.contains("image.tmdb.org") {
            let tmdbSize = currentTmdbSize(for: imageType ?? .poster)
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

    private static func currentTmdbSize(for imageType: TmdbImageType) -> String {
        let qualityString = UserDefaults.standard.string(forKey: "image.quality") ?? "AUTO"
        let screenScale = UIScreen.main.scale

        switch (qualityString, imageType) {
        case ("AUTO", .backdrop):
            return screenScale >= 3 ? "/original/" : "/w780/"
        case ("AUTO", _):
            return screenScale >= 3 ? "/w780/" : "/w500/"
        case ("HIGH", .backdrop):
            return "/original/"
        case ("HIGH", _):
            return "/w780/"
        case ("LOW", .backdrop):
            return "/w300/"
        case ("LOW", _):
            return "/w185/"
        case ("MEDIUM", .backdrop):
            return "/w780/"
        case ("MEDIUM", _):
            return "/w500/"
        default:
            return "/w500/"
        }
    }
}
