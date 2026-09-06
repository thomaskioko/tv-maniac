import DesignSystem
import Nuke
import NukeUI
import SwiftUI

public struct ListCollageItem: Identifiable, Equatable {
    public let id: Int64
    public let name: String
    public let itemCountLabel: String
    public let posterUrls: [String]

    public init(
        id: Int64,
        name: String,
        itemCountLabel: String,
        posterUrls: [String]
    ) {
        self.id = id
        self.name = name
        self.itemCountLabel = itemCountLabel
        self.posterUrls = posterUrls
    }
}

public struct ListCollageCard: View {
    public static let cardSize = CGSize(width: DimensionConstants.cardWidth, height: DimensionConstants.cardHeight)

    @Environment(\.appTheme) private var theme

    private let list: ListCollageItem
    private let fillsAvailableWidth: Bool
    private let onClick: () -> Void

    public init(list: ListCollageItem, fillsAvailableWidth: Bool = false, onClick: @escaping () -> Void) {
        self.list = list
        self.fillsAvailableWidth = fillsAvailableWidth
        self.onClick = onClick
    }

    public var body: some View {
        Button(action: onClick) {
            cardBody
                .overlay {
                    LinearGradient(
                        colors: [.clear, .black.opacity(0.85)],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                }
                .overlay(alignment: .bottomLeading) {
                    VStack(alignment: .leading, spacing: theme.spacing.xxxSmall) {
                        Text(list.name)
                            .textStyle(theme.typography.titleSmall)
                            .fontWeight(.bold)
                            .foregroundStyle(.white)
                            .lineLimit(1)

                        Text(list.itemCountLabel)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundStyle(.white.opacity(0.85))
                            .lineLimit(1)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(theme.spacing.small)
                }
                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large, style: .continuous))
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private var cardBody: some View {
        if fillsAvailableWidth {
            collage
                .frame(maxWidth: .infinity, minHeight: DimensionConstants.cardHeight, maxHeight: DimensionConstants.cardHeight)
        } else {
            collage
                .frame(width: DimensionConstants.cardWidth, height: DimensionConstants.cardHeight)
        }
    }

    @ViewBuilder
    private var collage: some View {
        switch list.posterUrls.count {
        case 0:
            placeholderCell
        case 1:
            posterCell(list.posterUrls[0])
        default:
            VStack(spacing: 0) {
                HStack(spacing: 0) {
                    cell(at: 0)
                    cell(at: 1)
                }
                HStack(spacing: 0) {
                    cell(at: 2)
                    cell(at: 3)
                }
            }
        }
    }

    @ViewBuilder
    private func cell(at index: Int) -> some View {
        if index < list.posterUrls.count {
            posterCell(list.posterUrls[index])
        } else {
            theme.colors.surface
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }

    private func posterCell(_ url: String) -> some View {
        LazyImage(url: ImageConfiguration.transformURL(url, imageType: .poster)) { state in
            if let image = state.image {
                image.resizable().scaledToFill()
            } else {
                theme.colors.surface
            }
        }
        .processors([.resize(
            size: CGSize(width: DimensionConstants.cardWidth, height: DimensionConstants.cardHeight),
            unit: .points
        )])
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .clipped()
    }

    private var placeholderCell: some View {
        theme.colors.surface
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .overlay {
                Image(systemName: "list.bullet")
                    .foregroundStyle(theme.colors.onSurfaceVariant.opacity(0.5))
            }
    }
}

private enum DimensionConstants {
    static let cardWidth: CGFloat = 200
    static let cardHeight: CGFloat = 130
}

#Preview {
    HStack {
        ListCollageCard(
            list: ListCollageItem(
                id: 1,
                name: "Watchlist",
                itemCountLabel: "24 shows",
                posterUrls: ["a", "b", "c", "d"]
            ),
            onClick: {}
        )
        ListCollageCard(
            list: ListCollageItem(
                id: 2,
                name: "Empty List",
                itemCountLabel: "0 shows",
                posterUrls: []
            ),
            onClick: {}
        )
    }
}
