import Foundation
import Kingfisher

public actor ImagePrefetchManager {
    public static let shared = ImagePrefetchManager()

    private var activePrefetches: [String: ImagePrefetcher] = [:]

    private init() {}

    public func prefetch(urls: [String], identifier: String) {
        guard !urls.isEmpty else { return }

        let transformedURLs = urls.compactMap { urlString -> URL? in
            let transformed = ImageConfiguration.transformURL(urlString)
            return URL(string: transformed)
        }

        guard !transformedURLs.isEmpty else { return }

        let prefetcher = ImagePrefetcher(
            urls: transformedURLs,
            options: [
                .cacheOriginalImage,
                .backgroundDecode,
            ]
        )
        prefetcher.maxConcurrentDownloads = 2

        activePrefetches[identifier]?.stop()
        activePrefetches[identifier] = prefetcher

        prefetcher.start()
    }

    public func cancelPrefetch(identifier: String) {
        let prefetcher = activePrefetches.removeValue(forKey: identifier)
        prefetcher?.stop()
    }

    public func cancelAllPrefetches() {
        let prefetchers = activePrefetches.values
        activePrefetches.removeAll()

        for prefetcher in prefetchers {
            prefetcher.stop()
        }
    }
}
