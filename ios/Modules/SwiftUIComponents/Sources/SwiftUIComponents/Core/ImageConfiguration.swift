import Foundation
import SDWebImage
import UIKit

/// Manages SDWebImage configuration and caching strategies
public enum ImageConfiguration {
    /// Image quality levels for cache and network optimization
    public enum Quality: String {
        case high = "HIGH"
        case medium = "MEDIUM"
        case low = "LOW"

        var diskCacheSize: UInt {
            let baseSize: UInt = 250 * 1024 * 1024 // 250MB
            switch self {
            case .high: return baseSize * 2 // 500MB
            case .medium: return baseSize // 250MB
            case .low: return baseSize / 2 // 125MB
            }
        }

        var memoryCacheSize: UInt {
            let baseSize: UInt = 50 * 1024 * 1024 // 50MB
            switch self {
            case .high: return baseSize * 2 // 100MB
            case .medium: return baseSize // 50MB
            case .low: return baseSize / 2 // 25MB
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

    /// Configures SDWebImage with the specified quality settings and all optimizations
    public static func configure(quality: Quality = .medium) {
        // Configure cache settings based on quality
        configureCacheSettings(quality: quality)

        // Configure SDWebImage optimizations
        configureOptimizations()

        // Configure download settings
        configureDownloadSettings()

        // Update the transformer with the new quality
        ImageURLTransformer.setTransformer { url in
            transformTMDBUrl(url, quality: quality)
        }
    }

    /// Configures cache settings based on quality
    private static func configureCacheSettings(quality: Quality) {
        // Configure cache duration (7 days)
        SDImageCache.shared.config.maxDiskAge = 3600 * 24 * 7

        // Configure disk cache size based on quality
        SDImageCache.shared.config.maxDiskSize = quality.diskCacheSize

        // Configure memory cache
        SDImageCache.shared.config.maxMemoryCost = quality.memoryCacheSize
        SDImageCache.shared.config.maxMemoryCount = 50

        // Enable aggressive cache purging for low quality
        if quality == .low {
            SDImageCache.shared.config.diskCacheExpireType = .accessDate
        }

        // Use memory mapping for disk cache reading
        SDImageCache.shared.config.diskCacheReadingOptions = .mappedIfSafe
    }

    /// Configures SDWebImage optimizations
    private static func configureOptimizations() {
        // Configure global options processor
        SDWebImageManager.shared.optionsProcessor = SDWebImageOptionsProcessor { _, options, context in
            var mutableOptions = options
            mutableOptions.insert(.scaleDownLargeImages)

            var mutableContext = context ?? [:]
            mutableContext[.imageForceDecodePolicy] = SDImageForceDecodePolicy.never.rawValue

            return SDWebImageOptionsResult(options: mutableOptions, context: mutableContext)
        }
    }

    /// Configures download settings
    private static func configureDownloadSettings() {
        // Configure download operation settings
        SDWebImageDownloader.shared.config.downloadTimeout = 15.0 // 15 seconds timeout
        SDWebImageDownloader.shared.config.maxConcurrentDownloads = 6 // Limit concurrent downloads
    }

    /// Sets up background memory management
    public static func setupBackgroundMemoryManagement() {
        // Clear memory cache when app enters background
        NotificationCenter.default.addObserver(
            forName: UIApplication.didEnterBackgroundNotification,
            object: nil,
            queue: .main
        ) { _ in
            SDImageCache.shared.clearMemory()
        }
    }

    private static func transformTMDBUrl(_ urlString: String, quality: Quality) -> String {
        guard urlString.contains("image.tmdb.org") else {
            return urlString
        }

        let sizeParam = quality.tmdbSizeParam
        var transformedURL = urlString

        // Replace width parameters (e.g., /w500/ -> /w185/)
        if let regex = try? NSRegularExpression(pattern: "/w\\d+/", options: []) {
            let range = NSRange(location: 0, length: transformedURL.utf16.count)
            transformedURL = regex.stringByReplacingMatches(
                in: transformedURL,
                options: [],
                range: range,
                withTemplate: "/\(sizeParam)/"
            )
        }

        // Replace /original/ if we're not using high quality
        if quality != .high {
            transformedURL = transformedURL.replacingOccurrences(
                of: "/original/",
                with: "/\(sizeParam)/"
            )
        }

        return transformedURL
    }

    /// Clears all image caches
    public static func clearCache() {
        SDImageCache.shared.clearMemory()
        SDImageCache.shared.clearDisk(onCompletion: nil)
    }

    /// Gets current cache size in MB
    public static func getCacheSizeInMB(completion: @escaping (Double) -> Void) {
        SDImageCache.shared.calculateSize { _, totalSize in
            let sizeInMB = Double(totalSize) / (1024.0 * 1024.0)
            completion(sizeInMB)
        }
    }
}
