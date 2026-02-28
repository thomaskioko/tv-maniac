import Foundation
import Nuke
import UIKit

/// Configures and manages the Nuke image pipeline and its caches.
///
/// Call ``configure()`` once during app launch (typically in `AppDelegate.init`)
/// to set up the shared pipeline with memory-aware limits. Cache management
/// methods are designed to be called from lifecycle observers:
///
/// - ``clearMemoryCache()`` on `didEnterBackground`
/// - ``handleMemoryWarning(escalationLevel:)`` on `didReceiveMemoryWarning`
///
/// Pipeline settings adapt automatically based on ``SystemMemory/isLowMemoryDevice``:
///
/// | Setting | Standard | Low Memory |
/// |---|---|---|
/// | Memory cache cost limit | 50 MB | 30 MB |
/// | Memory cache count limit | 50 | 30 |
/// | Memory cache TTL | 120 s | 60 s |
/// | Concurrent loads | 4 | 3 |
/// | URL cache (memory) | 5 MB | 2 MB |
public enum ImageCacheManager {
    private static var isConfigured = false

    /// Configures the shared `ImagePipeline` with memory-aware limits.
    /// Safe to call multiple times — subsequent calls are no-ops.
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

    /// Removes all entries from both the in-memory image cache and the on-disk data cache.
    public static func clearCache() {
        ImagePipeline.shared.cache.removeAll()
        if let dataCache = ImagePipeline.shared.configuration.dataCache {
            dataCache.removeAll()
        }
    }

    /// Clears in-memory caches only (Nuke image cache + URL cache).
    /// Disk cache is preserved so images can be restored without network requests.
    public static func clearMemoryCache() {
        ImageCache.shared.removeAll()
        URLCache.shared.removeAllCachedResponses()
    }

    /// Progressively evicts cached data based on how many consecutive memory
    /// warnings have been received (tracked by ``MemoryMonitor``).
    ///
    /// - Parameter escalationLevel: Warning count from `MemoryMonitor.recordMemoryWarning()`.
    ///   - **1** — Drop decoded images from the Nuke memory cache.
    ///   - **2** — Clear all in-memory caches (Nuke + URL cache).
    ///   - **3+** — Full purge including the on-disk data cache.
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
}
