import SwiftUI

public struct CastCardView: View {
    @Theme private var theme

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
            .shadow(color: theme.colors.outline.opacity(0.3), radius: 2.5, x: 0, y: 2)
    }

    private var profileImage: some View {
        CachedAsyncImage(url: profileUrl) { image in
            image.resizable()
        } placeholder: {
            profilePlaceholder
        }
        .aspectRatio(contentMode: .fill)
        .frame(width: 120, height: 160)
    }

    private var profilePlaceholder: some View {
        ZStack {
            Rectangle().fill(.gray.gradient)
            Image(systemName: "person")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 50, height: 50)
                .foregroundColor(.white)
        }
    }

    private var nameOverlay: some View {
        ZStack(alignment: .bottom) {
            LinearGradient(
                colors: [.clear, .black.opacity(0.2)], startPoint: .top, endPoint: .bottom
            )
            Rectangle()
                .fill(.ultraThinMaterial)
                .frame(height: 80)
                .mask(overlayMask)
            nameView
        }
    }

    private var overlayMask: some View {
        VStack(spacing: 0) {
            LinearGradient(
                colors: [.clear, .black], startPoint: .top, endPoint: .bottom
            )
            .frame(height: 60)
            Rectangle()
        }
    }

    private var nameView: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            Text(name)
                .textStyle(theme.typography.bodyMedium)
            Text(characterName)
                .textStyle(theme.typography.labelSmall)
        }
        .foregroundColor(.white)
        .lineLimit(1)
        .padding(.horizontal, 6)
        .padding(.bottom, theme.spacing.medium)
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
