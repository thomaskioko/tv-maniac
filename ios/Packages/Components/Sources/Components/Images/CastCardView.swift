import DesignSystem
import SwiftUI

public struct CastCardView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private var cardWidth: CGFloat {
        ImageType.cast.width(widthSizeClass)
    }

    private var cardHeight: CGFloat {
        cardWidth / ImageType.cast.aspect
    }

    private let profileUrl: String?
    private let name: String
    private let characterName: String

    public init(profileUrl: String?, name: String, characterName: String) {
        self.profileUrl = profileUrl
        self.name = name
        self.characterName = characterName
    }

    public var body: some View {
        profileImage
            .overlay(nameOverlay)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.small, style: .continuous))
            .appShadow(theme.shadows.large, color: theme.colors.outline.opacity(0.3))
    }

    private var profileImage: some View {
        LazyResizableImage(
            url: profileUrl,
            imageType: .profile,
            size: CGSize(width: cardWidth, height: cardHeight),
            placeholderIcon: "person"
        )
        .aspectRatio(contentMode: .fill)
        .frame(width: cardWidth, height: cardHeight)
    }

    private var nameOverlay: some View {
        ZStack(alignment: .bottom) {
            LinearGradient(
                colors: [.clear, theme.colors.surface.opacity(0.7)],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 80)
            .frame(maxHeight: .infinity, alignment: .bottom)
            nameView
        }
    }

    private var nameView: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxxSmall) {
            Text(name)
                .textStyle(theme.typography.bodyMedium)
            Text(characterName)
                .textStyle(theme.typography.bodyMedium)
        }
        .foregroundStyle(.appOnSurface)
        .lineLimit(1)
        .padding(.horizontal, theme.spacing.xSmall)
        .padding(.bottom, theme.spacing.xSmall)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

#Preview {
    VStack {
        CastCardView(
            profileUrl: nil,
            name: "Rosario Dawson",
            characterName: "Claire Temple"
        )
        CastCardView(
            profileUrl: "https://image.tmdb.org/t/p/w780/1mm7JGHIUX3GRRGXEV9QCzsI0ao.jpg",
            name: "Rosario Dawson",
            characterName: "Claire Temple"
        )
    }
}
