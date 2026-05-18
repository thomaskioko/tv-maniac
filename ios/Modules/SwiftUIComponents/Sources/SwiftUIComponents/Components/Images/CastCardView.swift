import DesignSystem
import SwiftUI

public struct CastCardView: View {
    @Environment(\.appTheme) private var theme

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
            .appShadow(theme.shadows.small, color: theme.colors.outline.opacity(0.3))
    }

    private var profileImage: some View {
        LazyResizableImage(
            url: profileUrl,
            imageType: .profile,
            size: CGSize(width: DimensionConstants.profileWidth, height: DimensionConstants.profileHeight),
            placeholderIcon: "person"
        )
        .aspectRatio(contentMode: .fill)
        .frame(width: DimensionConstants.profileWidth, height: DimensionConstants.profileHeight)
    }

    private var nameOverlay: some View {
        ZStack(alignment: .bottom) {
            LinearGradient(
                colors: [.clear, theme.colors.scrim.opacity(0.7)],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 80)
            .frame(maxHeight: .infinity, alignment: .bottom)
            nameView
        }
    }

    private var nameView: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            Text(name)
                .textStyle(theme.typography.bodyMedium)
            Text(characterName)
                .textStyle(theme.typography.labelSmall)
        }
        .foregroundStyle(.appOnScrim)
        .lineLimit(1)
        .padding(.horizontal, theme.spacing.xxSmall)
        .padding(.bottom, theme.spacing.medium)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private enum DimensionConstants {
    static let profileWidth: CGFloat = 120
    static let profileHeight: CGFloat = 160
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
