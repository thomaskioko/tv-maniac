import SwiftUI

public struct PosterItemView: View {
    @Theme private var theme

    private let title: String?
    private let posterUrl: String?
    private let libraryImageOverlay: String
    private let isInLibrary: Bool
    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let posterRadius: CGFloat?
    private let processorHeight: CGFloat?

    public init(
        title: String?,
        posterUrl: String?,
        libraryImageOverlay: String = "square.stack.fill",
        isInLibrary: Bool = false,
        posterWidth: CGFloat = 120,
        posterHeight: CGFloat = 180,
        posterRadius: CGFloat? = nil,
        processorHeight: CGFloat? = nil
    ) {
        self.title = title
        self.posterUrl = posterUrl
        self.libraryImageOverlay = libraryImageOverlay
        self.isInLibrary = isInLibrary
        self.posterWidth = posterWidth
        self.posterHeight = posterHeight
        self.posterRadius = posterRadius
        self.processorHeight = processorHeight
    }

    public var body: some View {
        let resolvedRadius = posterRadius ?? theme.shapes.small
        let imageHeight = processorHeight ?? posterHeight

        LazyResizableImage(
            url: posterUrl,
            size: CGSize(width: posterWidth, height: imageHeight)
        ) { state in
            if let image = state.image {
                image.resizable()
            } else {
                PosterPlaceholder(
                    title: title,
                    posterWidth: posterWidth,
                    posterHeight: imageHeight,
                    posterRadius: resolvedRadius
                )
            }
        }
        .scaledToFill()
        .clipShape(RoundedRectangle(cornerRadius: resolvedRadius, style: .continuous))
        .frame(width: posterWidth, height: posterHeight)
        .clipped()
        .overlay {
            if isInLibrary {
                LibraryOverlay(libraryImageOverlay: libraryImageOverlay)
            }
        }
    }
}

@ViewBuilder
private func LibraryOverlay(libraryImageOverlay: String) -> some View {
    VStack {
        HStack {
            Spacer()
            Image(systemName: libraryImageOverlay)
                .imageScale(.medium)
                .foregroundColor(.white)
                .padding(8)
        }
        Spacer()
    }
}

private enum DimensionConstants {
    static let shadowRadius: CGFloat = 2
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
