import SwiftUI

public struct FeaturedContentPosterView: View {
    @Theme private var theme

    private let showId: Int64
    private let title: String
    private let posterImageUrl: String?
    private let isInLibrary: Bool
    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let posterRadius: CGFloat?
    private let onClick: (Int64) -> Void

    public init(
        showId: Int64,
        title: String,
        posterImageUrl: String?,
        isInLibrary: Bool,
        posterWidth: CGFloat = 260,
        posterHeight: CGFloat = 460,
        posterRadius: CGFloat? = nil,
        onClick: @escaping (Int64) -> Void
    ) {
        self.showId = showId
        self.title = title
        self.posterImageUrl = posterImageUrl
        self.isInLibrary = isInLibrary
        self.posterWidth = posterWidth
        self.posterHeight = posterHeight
        self.posterRadius = posterRadius
        self.onClick = onClick
    }

    private var resolvedRadius: CGFloat {
        posterRadius ?? theme.shapes.large
    }

    public var body: some View {
        CachedAsyncImage(
            url: posterImageUrl,
            priority: .high
        ) { image in
            image.resizable()
        } placeholder: {
            PosterPlaceholder(
                title: title,
                posterWidth: posterWidth,
                posterHeight: posterHeight,
                posterRadius: resolvedRadius
            )
        }
        .aspectRatio(contentMode: .fill)
        .overlay {
            if isInLibrary {
                VStack {
                    Spacer()
                    HStack {
                        Spacer()

                        Image(systemName: "square.stack.fill")
                            .imageScale(.large)
                            .foregroundColor(theme.colors.onPrimary.opacity(0.9))
                            .padding([.vertical])
                            .padding(.trailing, theme.spacing.medium)
                            .textStyle(theme.typography.bodySmall)
                    }
                    .background {
                        theme.colors.imageGradient()
                    }
                }
                .frame(width: posterWidth)
            }
        }
        .frame(width: posterWidth, height: posterHeight)
        .clipShape(
            RoundedRectangle(cornerRadius: resolvedRadius, style: .continuous)
        )
        .onTapGesture {
            onClick(showId)
        }
    }
}

#Preview {
    FeaturedContentPosterView(
        showId: 2123,
        title: "Arcane",
        posterImageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
        isInLibrary: true,
        posterWidth: 460,
        onClick: { _ in }
    )
}
