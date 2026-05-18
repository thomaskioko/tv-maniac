import DesignSystem
import SwiftUI

public struct ShowContentItemView: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let imageUrl: String?
    private let imageWidth: CGFloat
    private let imageHeight: CGFloat
    private let shadowToken: TvManiacShadowToken?
    private let cornerRadius: CGFloat?
    private let imageRadius: CGFloat?

    public init(
        title: String,
        imageUrl: String?,
        imageWidth: CGFloat = 240,
        imageHeight: CGFloat = 180,
        shadowToken: TvManiacShadowToken? = nil,
        cornerRadius: CGFloat? = nil,
        imageRadius: CGFloat? = nil
    ) {
        self.imageUrl = imageUrl
        self.title = title
        self.imageWidth = imageWidth
        self.imageHeight = imageHeight
        self.shadowToken = shadowToken
        self.cornerRadius = cornerRadius
        self.imageRadius = imageRadius
    }

    public var body: some View {
        PosterItemView(
            title: nil,
            posterUrl: imageUrl,
            posterWidth: imageWidth,
            posterHeight: imageHeight,
            posterRadius: imageRadius ?? theme.shapes.small
        )
        .overlay(nameOverlay)
        .clipShape(RoundedRectangle(cornerRadius: cornerRadius ?? theme.shapes.small, style: .continuous))
        .appShadow(shadowToken ?? theme.shadows.small, color: theme.colors.surfaceVariant.opacity(0.3))
    }

    private var nameOverlay: some View {
        ZStack(alignment: .bottom) {
            LinearGradient(
                colors: [.clear, theme.colors.scrim.opacity(0.7)],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 60)
            .frame(maxHeight: .infinity, alignment: .bottom)

            MetallicTitleView(title: title)
        }
    }
}

private struct MetallicTitleView: View {
    @Environment(\.appTheme) private var theme
    let title: String

    var body: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            Text(title)
                .textStyle(theme.typography.titleSmall)
                .lineLimit(DimensionConstants.lineLimit)
                .foregroundStyle(
                    .linearGradient(
                        colors: MetallicGradient.chromeStops,
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .shadow(color: Color.black.opacity(0.4), radius: 1, x: 0.5, y: 0.5)
                .overlay {
                    Text(title)
                        .textStyle(theme.typography.titleSmall)
                        .lineLimit(DimensionConstants.lineLimit)
                        .foregroundStyle(
                            .linearGradient(
                                colors: [
                                    Color.white.opacity(0.7),
                                    .clear,
                                ],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                }
        }
        .padding(.horizontal, theme.spacing.xSmall)
        .padding(.bottom, theme.spacing.xSmall)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private enum DimensionConstants {
    static let imageRadius: CGFloat = 2.5
    static let cornerRadius: CGFloat = 4
    static let lineLimit: Int = 1
}

#Preview {
    VStack {
        ShowContentItemView(
            title: "The Penguin",
            imageUrl: "https://image.tmdb.org/t/p/w780/VSRmtRlYgd0pBISf7d34TAwWgB.jpg"
        )
        ShowContentItemView(
            title: "The Penguin",
            imageUrl: nil
        )
    }
}
