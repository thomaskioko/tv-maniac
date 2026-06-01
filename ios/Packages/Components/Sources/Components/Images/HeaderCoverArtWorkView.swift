import DesignSystem
import Nuke
import NukeUI
import SwiftUI

public struct HeaderCoverArtWorkView: View {
    @Environment(\.appTheme) private var theme

    private let imageUrl: String?
    private let posterHeight: CGFloat
    private let cornerRadius: CGFloat

    public init(
        imageUrl: String?,
        posterHeight: CGFloat,
        cornerRadius: CGFloat = 0
    ) {
        self.imageUrl = imageUrl
        self.posterHeight = posterHeight
        self.cornerRadius = cornerRadius
    }

    public var body: some View {
        LazyImage(url: ImageConfiguration.transformURL(imageUrl ?? "", imageType: .backdrop)) { state in
            if let image = state.image {
                image.resizable().scaledToFill()
            } else {
                placeholder
            }
        }
        .processors([.resize(
            size: CGSize(width: DimensionConstants.posterWidth, height: DimensionConstants.fixedImageHeight),
            unit: .points
        )])
        .frame(width: DimensionConstants.posterWidth, height: posterHeight)
        .clipped()
    }

    private var placeholder: some View {
        LinearGradient(
            colors: [theme.colors.grey.opacity(0.8), theme.colors.grey],
            startPoint: .top,
            endPoint: .bottom
        )
        .overlay {
            Image(systemName: "film")
                .textStyle(theme.typography.titleLarge)
                .fontWidth(.expanded)
                .foregroundStyle(.white.opacity(0.8))
        }
    }
}

private enum DimensionConstants {
    static let posterWidth: CGFloat = UIScreen.main.bounds.width
    static let fixedImageHeight: CGFloat = 600
    static let shadowRadius: CGFloat = 2
    static let cornerRadius: CGFloat = 8
}

#Preview {
    VStack {
        HeaderCoverArtWorkView(
            imageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            posterHeight: 320
        )

        Spacer()
    }
}
