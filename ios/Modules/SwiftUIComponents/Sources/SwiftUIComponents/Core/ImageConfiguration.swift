import Foundation
import SDWebImage

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

    public static func configure(quality: Quality = .medium) {
        // Configure cache duration (7 days)
        SDImageCache.shared.config.maxDiskAge = 3600 * 24 * 7

        // Configure disk cache size based on quality
        SDImageCache.shared.config.maxDiskSize = quality.diskCacheSize

        SDImageCache.shared.config.maxMemoryCost = quality.memoryCacheSize
        SDImageCache.shared.config.maxMemoryCount = 50

        if quality == .low {
            SDImageCache.shared.config.diskCacheExpireType = .accessDate
        }

        ImageURLTransformer.setTransformer { url in
            transformTMDBUrl(url, quality: quality)
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
