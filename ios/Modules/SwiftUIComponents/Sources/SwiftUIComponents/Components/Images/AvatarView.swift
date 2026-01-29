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
        borderWidth: CGFloat = 2
    ) {
        self.avatarUrl = avatarUrl
        self.size = size
        self.borderColor = borderColor
        self.borderWidth = borderWidth
    }

    public var body: some View {
        let resolvedBorderColor = borderColor ?? theme.colors.onPrimary

        CachedAsyncImage(
            url: avatarUrl,
            showIndicator: true
        ) {
            placeholderView(resolvedBorderColor)
        }
        .scaledToFill()
        .frame(width: size, height: size)
        .clipShape(Circle())
        .overlay(
            Circle()
                .stroke(resolvedBorderColor, lineWidth: borderWidth)
        )
    }

    private func placeholderView(_ borderColor: Color) -> some View {
        Image(systemName: "person")
            .font(.system(size: size * 0.5))
            .foregroundColor(borderColor)
            .frame(width: size, height: size)
            .background(theme.colors.surfaceVariant.opacity(0.3))
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
