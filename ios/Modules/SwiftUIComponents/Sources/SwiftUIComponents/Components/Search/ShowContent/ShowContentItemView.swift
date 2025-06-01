import SwiftUI

public struct ShowContentItemView: View {
    private let title: String
    private let imageUrl: String?
    private let imageWidth: CGFloat
    private let imageHeight: CGFloat
    private let shadowRadius: CGFloat
    private let cornerRadius: CGFloat
    private let imageRadius: CGFloat

    public init(
        title: String,
        imageUrl: String?,
        imageWidth: CGFloat = 260,
        imageHeight: CGFloat = 200,
        shadowRadius: CGFloat = 2.5,
        cornerRadius: CGFloat = 4,
        imageRadius: CGFloat = 2.5
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
            posterRadius: imageRadius
        )
        .overlay(nameOverlay)
        .clipShape(RoundedRectangle(cornerRadius: DimensionConstants.cornerRadius, style: .continuous))
        .shadow(color: Color.grey200.opacity(0.3), radius: DimensionConstants.shadowRadius, x: 0, y: 2)
    }

    private var nameOverlay: some View {
        ZStack(alignment: .bottom) {
            LinearGradient(
                colors: [.clear, .black.opacity(0.2)], startPoint: .top, endPoint: .bottom
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
                colors: [.clear, .black], startPoint: .top, endPoint: .bottom
            )
            .frame(height: 40)
            Rectangle()
        }
    }
}

private struct MetallicTitleView: View {
    let title: String

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.headline)
                .lineLimit(DimensionConstants.lineLimit)
                .fontWeight(.semibold)
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
                .shadow(color: .black.opacity(0.4), radius: 1, x: 0.5, y: 0.5)
                // Add shine effect
                .overlay {
                    Text(title)
                        .font(.headline)
                        .lineLimit(DimensionConstants.lineLimit)
                        .fontWeight(.semibold)
                        .foregroundStyle(
                            .linearGradient(
                                colors: [
                                    .white.opacity(0.7),
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
