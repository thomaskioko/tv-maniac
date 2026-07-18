import DesignSystem
import SwiftUI

public struct PosterItemView: View {
    @Environment(\.appTheme) private var theme

    private let title: String?
    private let posterUrl: String?
    private let libraryImageOverlay: String
    private let isInLibrary: Bool
    private let imageType: TmdbImageType
    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let aspectRatio: CGFloat?
    private let posterRadius: CGFloat?
    private let processorHeight: CGFloat?

    public init(
        title: String?,
        posterUrl: String?,
        libraryImageOverlay: String = "square.stack.fill",
        isInLibrary: Bool = false,
        imageType: TmdbImageType = .poster,
        posterWidth: CGFloat = 120,
        posterHeight: CGFloat = 180,
        aspectRatio: CGFloat? = nil,
        posterRadius: CGFloat? = nil,
        processorHeight: CGFloat? = nil
    ) {
        self.title = title
        self.posterUrl = posterUrl
        self.libraryImageOverlay = libraryImageOverlay
        self.isInLibrary = isInLibrary
        self.imageType = imageType
        self.posterWidth = posterWidth
        self.posterHeight = posterHeight
        self.aspectRatio = aspectRatio
        self.posterRadius = posterRadius
        self.processorHeight = processorHeight
    }

    public var body: some View {
        let resolvedRadius = posterRadius ?? ImageDimens.posterCornerRadius
        let resolvedHeight = aspectRatio.map { posterWidth / $0 } ?? posterHeight
        let imageHeight = processorHeight ?? resolvedHeight

        poster(radius: resolvedRadius, imageHeight: imageHeight)
            .overlay {
                if isInLibrary {
                    LibraryOverlay(libraryImageOverlay: libraryImageOverlay, inset: theme.spacing.xSmall)
                }
            }
            .appShadow(theme.shadows.medium)
    }

    @ViewBuilder
    private func poster(radius: CGFloat, imageHeight: CGFloat) -> some View {
        let image = LazyResizableImage(
            url: posterUrl,
            imageType: imageType,
            size: CGSize(width: posterWidth, height: imageHeight),
            aspectRatio: aspectRatio,
            placeholderTitle: title
        )

        if aspectRatio != nil {
            image
                .clipShape(RoundedRectangle(cornerRadius: radius, style: .continuous))
        } else {
            image
                .scaledToFill()
                .clipShape(RoundedRectangle(cornerRadius: radius, style: .continuous))
                .frame(width: posterWidth, height: posterHeight)
                .clipped()
        }
    }
}

private func LibraryOverlay(libraryImageOverlay: String, inset: CGFloat) -> some View {
    VStack {
        HStack {
            Spacer()
            Image(systemName: libraryImageOverlay)
                .imageScale(.medium)
                .foregroundStyle(.white)
                .padding(inset)
        }
        Spacer()
    }
}

#Preview {
    VStack {
        PosterItemView(
            title: "Arcane",
            posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            isInLibrary: true,
            posterWidth: 160,
            posterHeight: 240
        )

        PosterItemView(
            title: "Arcane",
            posterUrl: nil,
            isInLibrary: true,
            posterWidth: 160,
            posterHeight: 240
        )
    }
}
