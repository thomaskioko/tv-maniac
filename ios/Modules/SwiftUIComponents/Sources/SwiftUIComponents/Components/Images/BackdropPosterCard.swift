import SwiftUI

public struct BackdropPosterCard: View {
    @Theme private var theme

    private let title: String
    private let posterUrl: String?
    private let isInLibrary: Bool
    private let cardWidth: CGFloat
    private let cardHeight: CGFloat

    public init(
        title: String,
        posterUrl: String?,
        isInLibrary: Bool = false,
        cardWidth: CGFloat = 240,
        cardHeight: CGFloat = 140
    ) {
        self.title = title
        self.posterUrl = posterUrl
        self.isInLibrary = isInLibrary
        self.cardWidth = cardWidth
        self.cardHeight = cardHeight
    }

    public var body: some View {
        PosterItemView(
            title: title,
            posterUrl: posterUrl,
            isInLibrary: isInLibrary,
            imageType: .poster,
            posterWidth: cardWidth,
            posterHeight: cardHeight,
            posterRadius: theme.shapes.small
        )
        .overlay(alignment: .bottom) {
            ZStack(alignment: .bottomLeading) {
                LinearGradient(
                    colors: [
                        Color.clear,
                        theme.colors.surface.opacity(0.4),
                        theme.colors.surface.opacity(0.7),
                        theme.colors.surface.opacity(0.9),
                        theme.colors.surface,
                    ],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 80)

                Text(title)
                    .textStyle(theme.typography.labelLarge)
                    .foregroundStyle(theme.colors.onSurface)
                    .lineLimit(1)
                    .truncationMode(.tail)
                    .padding(.horizontal, theme.spacing.medium)
                    .padding(.bottom, theme.spacing.xSmall)
            }
            .clipShape(
                UnevenRoundedRectangle(
                    bottomLeadingRadius: theme.shapes.small,
                    bottomTrailingRadius: theme.shapes.small,
                    style: .continuous
                )
            )
        }
    }
}

#Preview {
    VStack {
        BackdropPosterCard(
            title: "Arcane",
            posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg"
        )

        BackdropPosterCard(
            title: "The Lord of the Rings: The Rings of Power",
            posterUrl: nil
        )
    }
}
