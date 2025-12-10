import SDWebImageSwiftUI
import SwiftUI

/// A reusable image component that wraps SDWebImage with optimized caching configuration.
///
/// Automatically handles synchronous disk cache queries, URL transformation via `ImageURLTransformer`,
/// placeholder display when URL is nil, and configurable loading priority.
public struct CachedAsyncImage<Content: View, Placeholder: View>: View {
    public enum Priority {
        case normal
        case high
    }

    private let url: String?
    private let priority: Priority
    private let showIndicator: Bool
    private let content: (Image) -> Content
    private let placeholder: () -> Placeholder

    /// - Parameters:
    ///   - url: The URL string of the image to load. If nil or empty, the placeholder is shown.
    ///   - priority: The loading priority. Use `.high` for featured content. Defaults to `.normal`.
    ///   - showIndicator: Whether to show an activity indicator while loading. Defaults to `false`.
    ///   - content: A closure that takes the loaded `Image` and returns the content view.
    ///   - placeholder: A closure that returns the placeholder view shown while loading or on error.
    public init(
        url: String?,
        priority: Priority = .normal,
        showIndicator: Bool = false,
        @ViewBuilder content: @escaping (Image) -> Content,
        @ViewBuilder placeholder: @escaping () -> Placeholder
    ) {
        self.url = url
        self.priority = priority
        self.showIndicator = showIndicator
        self.content = content
        self.placeholder = placeholder
    }

    private var webImageOptions: SDWebImageOptions {
        var options: SDWebImageOptions = [
            .queryDiskDataSync,
            .scaleDownLargeImages,
            .retryFailed,
        ]
        if priority == .high {
            options.insert(.highPriority)
        }
        return options
    }

    public var body: some View {
        if let url, !url.isEmpty, let imageURL = URL(string: url.transformedImageURL) {
            if showIndicator {
                WebImage(url: imageURL, options: webImageOptions) { image in
                    content(image)
                } placeholder: {
                    placeholder()
                }
                .indicator(.activity)
                .transition(.opacity)
            } else {
                WebImage(url: imageURL, options: webImageOptions) { image in
                    content(image)
                } placeholder: {
                    placeholder()
                }
                .transition(.opacity)
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
        showIndicator: Bool = false,
        @ViewBuilder content: @escaping (Image) -> Content
    ) {
        self.init(
            url: url,
            priority: priority,
            showIndicator: showIndicator,
            content: content,
            placeholder: { EmptyView() }
        )
    }
}
