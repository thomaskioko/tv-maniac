import CoreKit
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

        var config = ImagePipeline.Configuration.withDataCache(name: "com.thomaskioko.tvmaniac.images")

        let imageCache = ImageCache()
        let isLowMem = SystemMemory.isLowMemoryDevice
        imageCache.costLimit = isLowMem ? 30 * 1024 * 1024 : 50 * 1024 * 1024
        imageCache.countLimit = isLowMem ? 30 : 50
        imageCache.ttl = isLowMem ? 60 : 120
        config.imageCache = imageCache

        config.isStoringPreviewsInMemoryCache = false
        config.isProgressiveDecodingEnabled = true
        config.isDecompressionEnabled = true
        config.dataCachePolicy = .automatic

        config.dataLoadingQueue.maxConcurrentOperationCount = isLowMem ? 3 : 4

        let urlCache = URLCache(
            memoryCapacity: isLowMem ? 2 * 1024 * 1024 : 5 * 1024 * 1024,
            diskCapacity: 100 * 1024 * 1024
        )
        config.dataLoader = DataLoader(configuration: {
            let configuration = DataLoader.defaultConfiguration
            configuration.urlCache = urlCache
            return configuration
        }())

        ImagePipeline.shared = ImagePipeline(configuration: config)
    }

    public static func clearCache() {
        ImagePipeline.shared.cache.removeAll()
        if let dataCache = ImagePipeline.shared.configuration.dataCache {
            dataCache.removeAll()
        }
    }

    public static func clearMemoryCache() {
        ImageCache.shared.removeAll()
        URLCache.shared.removeAllCachedResponses()
    }

    public static func handleMemoryWarning(escalationLevel: Int) {
        switch escalationLevel {
        case 1:
            ImageCache.shared.removeAll()
        case 2:
            clearMemoryCache()
        default:
            clearCache()
        }
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
