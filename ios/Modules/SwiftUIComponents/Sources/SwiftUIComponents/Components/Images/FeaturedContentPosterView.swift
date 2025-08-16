import SDWebImageSwiftUI
import SwiftUI

public struct FeaturedContentPosterView: View {
    private let showId: Int64
    private let title: String
    private let posterImageUrl: String?
    private let isInLibrary: Bool
    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let posterRadius: CGFloat
    private let onClick: (Int64) -> Void

    public init(
        showId: Int64,
        title: String,
        posterImageUrl: String?,
        isInLibrary: Bool,
        posterWidth: CGFloat = 260,
        posterHeight: CGFloat = 460,
        posterRadius: CGFloat = 12,
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

    public var body: some View {
        if let posterUrl = posterImageUrl {
            WebImage(url: URL(string: posterUrl.transformedImageURL), options: .highPriority) { image in
                image.resizable()
            } placeholder: {
                PosterPlaceholder(
                    title: title,
                    posterWidth: posterWidth,
                    posterHeight: posterHeight,
                    posterRadius: posterRadius
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
                                .foregroundColor(.white.opacity(0.9))
                                .padding([.vertical])
                                .padding(.trailing, 16)
                                .font(.caption)
                        }
                        .background {
                            Color.black.opacity(0.6)
                                .mask {
                                    LinearGradient(colors:
                                        [Color.black,
                                         Color.black.opacity(0.924),
                                         Color.black.opacity(0.707),
                                         Color.black.opacity(0.383),
                                         Color.black.opacity(0)],
                                        startPoint: .bottom,
                                        endPoint: .top)
                                }
                        }
                    }
                    .frame(width: posterWidth)
                }
            }
            .transition(.opacity)
            .frame(width: posterWidth, height: posterHeight)
            .clipShape(
                RoundedRectangle(cornerRadius: posterRadius, style: .continuous)
            )
            .onTapGesture {
                onClick(showId)
            }
        } else {
            PosterPlaceholder(
                title: title,
                posterWidth: posterWidth,
                posterHeight: posterHeight,
                posterRadius: posterRadius
            )
            .onTapGesture {
                onClick(showId)
            }
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
