import SwiftUI

/// Kind of image surface. Bundles the size-class-keyed width (``width(_:)``) with the matching
/// aspect ratio (``aspect``) so a call site declares the kind once instead of pairing an
/// `ImageDimens` width with an `ImageDimens` aspect by hand. This is layout sizing only; image
/// fetching/bucket selection is a separate concern handled by `TmdbImageType`.
public enum ImageType {
    case poster
    case backdrop
    case cast

    /// Aspect ratio for the kind, expressed as width / height (so `height = width / aspect`).
    public var aspect: CGFloat {
        switch self {
        case .poster: ImageDimens.posterAspect
        case .backdrop: ImageDimens.backdropAspect
        case .cast: ImageDimens.castAspect
        }
    }

    /// Width for the kind at the given window width size class.
    public func width(_ sizeClass: WidthSizeClass) -> CGFloat {
        switch self {
        case .poster: ImageDimens.posterWidth(sizeClass)
        case .backdrop: ImageDimens.backdropCardWidth(sizeClass)
        case .cast: ImageDimens.castCardWidth(sizeClass)
        }
    }
}
