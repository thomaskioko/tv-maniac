import DesignSystem
import SwiftUI

public struct BackdropPosterCard: View {
    @Environment(\.appTheme) private var theme

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
            title: nil,
            posterUrl: posterUrl,
            isInLibrary: isInLibrary,
            imageType: .poster,
            posterWidth: cardWidth,
            posterHeight: cardHeight,
            posterRadius: 0
        )
        .overlay(alignment: .bottom) {
            ZStack(alignment: .bottomLeading) {
                LinearGradient(
                    colors: [.clear, theme.colors.scrim.opacity(0.7)],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 80)

                Text(title)
                    .textStyle(theme.typography.labelLarge)
                    .foregroundStyle(.appOnScrim)
                    .lineLimit(1)
                    .truncationMode(.tail)
                    .padding(.horizontal, theme.spacing.medium)
                    .padding(.bottom, theme.spacing.xSmall)
            }
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
