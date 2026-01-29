import Kingfisher
import SwiftUI

public struct CachedAsyncImage<Placeholder: View>: View {
    public enum Priority {
        case normal
        case high
    }

    private let url: String?
    private let priority: Priority
    private let showIndicator: Bool
    private let placeholder: () -> Placeholder

    /// - Parameters:
    ///   - url: The URL string of the image to load. If nil or empty, the placeholder is shown.
    ///   - priority: The loading priority. Use `.high` for featured content. Defaults to `.normal`.
    ///   - showIndicator: Whether to show an activity indicator while loading. Defaults to `false`.
    ///   - placeholder: A closure that returns the placeholder view shown while loading or on error.
    public init(
        url: String?,
        priority: Priority = .normal,
        showIndicator: Bool = false,
        @ViewBuilder placeholder: @escaping () -> Placeholder
    ) {
        self.url = url
        self.priority = priority
        self.showIndicator = showIndicator
        self.placeholder = placeholder
    }

    private var imageURL: URL? {
        guard let url, !url.isEmpty else { return nil }
        let transformed = ImageConfiguration.transformURL(url)
        return URL(string: transformed)
    }

    public var body: some View {
        if let imageURL {
            configuredImage(for: imageURL)
        } else {
            placeholder()
        }
    }

    private func configuredImage(for url: URL) -> some View {
        KFImage(url)
            .placeholder { placeholderContent }
            .loadDiskFileSynchronously()
            .fade(duration: 0.15)
            .retry(maxCount: 2, interval: .seconds(1))
            .cacheOriginalImage()
            .downloadPriority(priority == .high ? 1.0 : 0.5)
            .cancelOnDisappear(false)
            .onFailure { error in
                #if DEBUG
                    print("Image load failed for \(url): \(error.localizedDescription)")
                #endif
            }
            .resizable()
    }

    @ViewBuilder
    private var placeholderContent: some View {
        if showIndicator {
            ZStack {
                placeholder()
                ProgressView()
            }
        } else {
            placeholder()
        }
    }
}

public extension CachedAsyncImage where Placeholder == EmptyView {
    init(
        url: String?,
        priority: Priority = .normal,
        showIndicator: Bool = false
    ) {
        self.init(
            url: url,
            priority: priority,
            showIndicator: showIndicator,
            placeholder: { EmptyView() }
        )
    }
}
