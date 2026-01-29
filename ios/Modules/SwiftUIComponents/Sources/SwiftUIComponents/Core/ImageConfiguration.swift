import Foundation
import Kingfisher
import UIKit

public enum ImageConfiguration {
    public enum Quality: String {
        case high = "HIGH"
        case medium = "MEDIUM"
        case low = "LOW"

        var diskCacheSize: UInt {
            let baseSize: UInt = 250 * 1024 * 1024
            switch self {
            case .high: return baseSize * 2
            case .medium: return baseSize
            case .low: return baseSize / 2
            }
        }

        var memoryCacheSize: Int {
            switch self {
            case .high: 50 * 1024 * 1024
            case .medium: 30 * 1024 * 1024
            case .low: 15 * 1024 * 1024
            }
        }

        var memoryCacheCount: Int {
            switch self {
            case .high: 50
            case .medium: 30
            case .low: 15
            }
        }

        var tmdbSizeParam: String {
            switch self {
            case .high: "original"
            case .medium: "w500"
            case .low: "w185"
            }
        }
    }

    private static var currentQuality: Quality = .medium

    public static func configure(quality: Quality = .medium) {
        currentQuality = quality
        configureCacheSettings(quality: quality)
        configureDownloadSettings()
    }

    private static func configureCacheSettings(quality: Quality) {
        let cache = ImageCache.default
        cache.memoryStorage.config.totalCostLimit = quality.memoryCacheSize
        cache.memoryStorage.config.countLimit = quality.memoryCacheCount
        cache.diskStorage.config.sizeLimit = quality.diskCacheSize
        cache.diskStorage.config.expiration = .days(7)

        if quality == .low {
            cache.diskStorage.config.expiration = .days(3)
        }
    }

    private static func configureDownloadSettings() {
        let downloader = ImageDownloader.default
        downloader.downloadTimeout = 15.0
    }

    public static func setupBackgroundMemoryManagement() {
        NotificationCenter.default.addObserver(
            forName: UIApplication.didEnterBackgroundNotification,
            object: nil,
            queue: .main
        ) { _ in handleBackgroundCleanup() }

        NotificationCenter.default.addObserver(
            forName: UIApplication.didReceiveMemoryWarningNotification,
            object: nil,
            queue: .main
        ) { _ in handleMemoryWarning() }
    }

    public static func handleBackgroundCleanup() {
        Task {
            await ImagePrefetchManager.shared.cancelAllPrefetches()
        }
        ImageCache.default.clearMemoryCache()
        ImageCache.default.cleanExpiredDiskCache()
    }

    public static func handleMemoryWarning() {
        ImageCache.default.clearMemoryCache()
        ImageDownloader.default.cancelAll()
    }

    public static func transformURL(_ urlString: String) -> String {
        guard urlString.contains("image.tmdb.org") else { return urlString }
        let sizeParam = currentQuality.tmdbSizeParam
        var result = urlString.replacingOccurrences(of: "/original/", with: "/\(sizeParam)/")
        if result == urlString {
            result = result.replacingOccurrences(
                of: #"/w\d+/"#,
                with: "/\(sizeParam)/",
                options: .regularExpression
            )
        }
        return result
    }

    public static func clearCache() {
        ImageCache.default.clearMemoryCache()
        ImageCache.default.clearDiskCache()
    }

    public static func getCacheSizeInMB(completion: @escaping (Double) -> Void) {
        ImageCache.default.calculateDiskStorageSize { result in
            switch result {
            case let .success(size):
                completion(Double(size) / (1024.0 * 1024.0))
            case .failure:
                completion(0)
            }
        }
    }
}
