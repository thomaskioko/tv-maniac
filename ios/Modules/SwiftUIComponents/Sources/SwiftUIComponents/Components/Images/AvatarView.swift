import SDWebImageSwiftUI
import SwiftUI

public struct AvatarView: View {
    private let avatarUrl: String?
    private let size: CGFloat
    private let borderColor: Color
    private let borderWidth: CGFloat

    public init(
        avatarUrl: String?,
        size: CGFloat = 32,
        borderColor: Color = .white,
        borderWidth: CGFloat = 2
    ) {
        self.avatarUrl = avatarUrl
        self.size = size
        self.borderColor = borderColor
        self.borderWidth = borderWidth
    }

    public var body: some View {
        Group {
            if let avatarUrl, !avatarUrl.isEmpty, let url = URL(string: avatarUrl) {
                WebImage(url: url) { image in
                    image
                        .resizable()
                        .scaledToFill()
                } placeholder: {
                    placeholderView
                }
                .indicator(.activity)
                .transition(.fade(duration: 0.3))
                .frame(width: size, height: size)
                .clipShape(Circle())
                .overlay(
                    Circle()
                        .stroke(borderColor, lineWidth: borderWidth)
                )
            } else {
                placeholderView
            }
        }
    }

    private var placeholderView: some View {
        Image(systemName: "person")
            .font(.system(size: size * 0.5))
            .foregroundColor(borderColor)
            .frame(width: size, height: size)
            .background(Color.gray.opacity(0.3))
            .clipShape(Circle())
            .overlay(
                Circle()
                    .stroke(borderColor, lineWidth: borderWidth)
            )
    }
}

#Preview {
    VStack(spacing: 20) {
        AvatarView(avatarUrl: nil)

        AvatarView(
            avatarUrl: "https://walter.trakt.tv/images/users/000/000/001/avatars/large/6d19b1d5e5.jpg",
            size: 48,
            borderColor: .blue
        )

        AvatarView(
            avatarUrl: nil,
            size: 64,
            borderColor: .accentColor,
            borderWidth: 3
        )
    }
}
