import SwiftUI

public struct AvatarView: View {
    @Theme private var theme

    private let avatarUrl: String?
    private let size: CGFloat
    private let borderColor: Color?
    private let borderWidth: CGFloat

    public init(
        avatarUrl: String?,
        size: CGFloat = 32,
        borderColor: Color? = nil,
        borderWidth: CGFloat = 0
    ) {
        self.avatarUrl = avatarUrl
        self.size = size
        self.borderColor = borderColor
        self.borderWidth = borderWidth
    }

    public var body: some View {
        let resolvedBorderColor = borderColor ?? theme.colors.onPrimary

        LazyResizableImage(
            url: avatarUrl,
            size: CGSize(width: size, height: size),
            placeholderIcon: "person"
        )
        .scaledToFill()
        .frame(width: size, height: size)
        .clipShape(Circle())
        .overlay(
            Circle()
                .stroke(resolvedBorderColor, lineWidth: borderWidth)
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
