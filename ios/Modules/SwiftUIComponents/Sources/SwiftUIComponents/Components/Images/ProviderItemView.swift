import DesignSystem
import SwiftUI

public struct ProviderItemView: View {
    @Environment(\.appTheme) private var theme

    private let logoUrl: String?
    private let imageWidth: CGFloat
    private let imageHeight: CGFloat
    private let imageRadius: CGFloat?
    private let shadowToken: TvManiacShadowToken?

    public init(
        logoUrl: String?,
        imageWidth: CGFloat = 80,
        imageHeight: CGFloat = 70,
        imageRadius: CGFloat? = nil,
        shadowToken: TvManiacShadowToken? = nil
    ) {
        self.logoUrl = logoUrl
        self.imageWidth = imageWidth
        self.imageHeight = imageHeight
        self.imageRadius = imageRadius
        self.shadowToken = shadowToken
    }

    private var resolvedRadius: CGFloat {
        imageRadius ?? theme.shapes.medium
    }

    public var body: some View {
        VStack(alignment: .leading) {
            LazyResizableImage(
                url: logoUrl,
                size: CGSize(width: imageWidth, height: imageHeight),
                placeholderIcon: "tv"
            )
            .padding(.horizontal, theme.spacing.xxSmall)
            .aspectRatio(contentMode: .fill)
            .frame(
                width: imageWidth,
                height: imageHeight
            )
            .clipped()
            .cornerRadius(resolvedRadius)
            .appShadow(shadowToken ?? theme.shadows.small, color: theme.colors.surfaceVariant.opacity(0.3))
        }
    }
}

#Preview {
    VStack {
        ProviderItemView(
            logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/aYkLXz4dxHgOrFNH7Jv7Cpy56Ms.png"
        )

        ProviderItemView(logoUrl: nil)
    }
}
