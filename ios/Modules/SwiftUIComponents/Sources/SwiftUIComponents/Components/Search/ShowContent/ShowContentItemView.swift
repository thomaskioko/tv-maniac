import SwiftUI

public struct ShowContentItemView: View {
    @Theme private var theme

    private let title: String
    private let imageUrl: String?
    private let imageWidth: CGFloat
    private let imageHeight: CGFloat
    private let shadowRadius: CGFloat
    private let cornerRadius: CGFloat?
    private let imageRadius: CGFloat?

    public init(
        title: String,
        imageUrl: String?,
        imageWidth: CGFloat = 260,
        imageHeight: CGFloat = 200,
        shadowRadius: CGFloat = 2.5,
        cornerRadius: CGFloat? = nil,
        imageRadius: CGFloat? = nil
    ) {
        self.imageUrl = imageUrl
        self.title = title
        self.imageWidth = imageWidth
        self.imageHeight = imageHeight
        self.shadowRadius = shadowRadius
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
        .shadow(color: theme.colors.surfaceVariant.opacity(0.3), radius: shadowRadius, x: 0, y: 2)
    }

    private var nameOverlay: some View {
        ZStack(alignment: .bottom) {
            LinearGradient(
                colors: [.clear, Color.black.opacity(0.2)], startPoint: .top, endPoint: .bottom
            )
            Rectangle()
                .fill(.ultraThinMaterial)
                .frame(height: 40)
                .mask(overlayMask)

            MetallicTitleView(title: title)
        }
    }

    private var overlayMask: some View {
        VStack(spacing: 0) {
            LinearGradient(
                colors: [.clear, Color.black], startPoint: .top, endPoint: .bottom
            )
            .frame(height: 40)
            Rectangle()
        }
    }
}

private struct MetallicTitleView: View {
    @Theme private var theme
    let title: String

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .textStyle(theme.typography.titleSmall)
                .lineLimit(DimensionConstants.lineLimit)
                .foregroundStyle(
                    .linearGradient(
                        colors: [
                            Color(white: 0.95),
                            Color(white: 0.85),
                            Color(white: 0.95),
                            Color(white: 0.75),
                        ],
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
        .padding(.horizontal, 8)
        .padding(.bottom, 10)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private enum DimensionConstants {
    static let imageRadius: CGFloat = 2.5
    static let cornerRadius: CGFloat = 4
    static let shadowRadius: CGFloat = 2.5
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
