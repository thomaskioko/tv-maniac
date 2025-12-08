import SDWebImageSwiftUI
import SwiftUI

public struct ProviderItemView: View {
    @Theme private var theme

    private let logoUrl: String?
    private let imageWidth: CGFloat
    private let imageHeight: CGFloat
    private let imageRadius: CGFloat?
    private let shadowRadius: CGFloat

    public init(
        logoUrl: String?,
        imageWidth: CGFloat = 80,
        imageHeight: CGFloat = 70,
        imageRadius: CGFloat? = nil,
        shadowRadius: CGFloat = 2.5
    ) {
        self.logoUrl = logoUrl
        self.imageWidth = imageWidth
        self.imageHeight = imageHeight
        self.imageRadius = imageRadius
        self.shadowRadius = shadowRadius
    }

    private var resolvedRadius: CGFloat {
        imageRadius ?? theme.shapes.medium
    }

    public var body: some View {
        VStack(alignment: .leading) {
            if let providerUrl = logoUrl {
                WebImage(url: URL(string: providerUrl)) { image in
                    image.resizable()
                        .padding(.horizontal, theme.spacing.xxSmall)
                } placeholder: { providerPlaceholder }
                    .aspectRatio(contentMode: .fill)
                    .frame(
                        width: imageWidth,
                        height: imageHeight
                    )
                    .clipped()
                    .cornerRadius(resolvedRadius)
                    .shadow(color: theme.colors.surfaceVariant.opacity(0.3), radius: shadowRadius, x: 0, y: 2)
            } else {
                providerPlaceholder
            }
        }
    }

    private var providerPlaceholder: some View {
        ZStack {
            RoundedRectangle(cornerRadius: resolvedRadius, style: .continuous)
                .fill(.gray.gradient)
                .frame(
                    width: imageWidth,
                    height: imageHeight
                )
            Image(systemName: "tv")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 24, height: 24)
                .foregroundColor(theme.colors.onPrimary)
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
