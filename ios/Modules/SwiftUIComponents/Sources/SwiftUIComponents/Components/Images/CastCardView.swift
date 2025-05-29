import SDWebImageSwiftUI
import SwiftUI

public struct CastCardView: View {
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
            .clipShape(RoundedRectangle(cornerRadius: DimensionConstants.cornerRadius, style: .continuous))
            .shadow(color: Color.grey200.opacity(0.3), radius: DimensionConstants.shadowRadius, x: 0, y: 2)
    }

    private var profileImage: some View {
        Group {
            if let profileUrl, let url = URL(string: profileUrl) {
                WebImage(url: url) { image in
                    image.resizable()
                } placeholder: {
                    profilePlaceholder
                }
                .transition(.opacity)
            } else {
                profilePlaceholder
            }
        }
        .aspectRatio(contentMode: .fill)
        .frame(width: DimensionConstants.profileWidth, height: DimensionConstants.profileHeight)
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
        VStack(alignment: .leading, spacing: 4) {
            Text(name)
                .font(.callout)
                .fontWeight(.semibold)
            Text(characterName)
                .font(.caption)
        }
        .foregroundColor(.white)
        .lineLimit(DimensionConstants.lineLimit)
        .padding(.horizontal, 6)
        .padding(.bottom)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private enum DimensionConstants {
    static let profileWidth: CGFloat = 120
    static let profileHeight: CGFloat = 160
    static let shadowRadius: CGFloat = 2.5
    static let cornerRadius: CGFloat = 4
    static let lineLimit: Int = 1
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
