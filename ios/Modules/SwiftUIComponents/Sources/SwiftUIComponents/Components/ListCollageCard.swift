import DesignSystem
import SwiftUI

/// Trakt-style list card: a 2x2 poster collage with the list name and item count overlaid on a
/// bottom scrim. Falls back gracefully when fewer than four posters are available.
public struct ListCollageCard: View {
    @Environment(\.appTheme) private var theme

    private let list: SwiftProfileList
    private let onClick: () -> Void

    public init(list: SwiftProfileList, onClick: @escaping () -> Void) {
        self.list = list
        self.onClick = onClick
    }

    public var body: some View {
        Button(action: onClick) {
            ZStack(alignment: .bottomLeading) {
                collage

                LinearGradient(
                    colors: [.clear, .black.opacity(0.85)],
                    startPoint: .top,
                    endPoint: .bottom
                )

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
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
                .padding(theme.spacing.small)
            }
            .frame(width: DimensionConstants.cardWidth, height: DimensionConstants.cardHeight)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large, style: .continuous))
        }
        .buttonStyle(.plain)
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
            placeholderCell
        }
    }

    private func posterCell(_ url: String) -> some View {
        LazyResizableImage(
            url: url,
            imageType: .poster,
            size: CGSize(width: DimensionConstants.cardWidth, height: DimensionConstants.cardHeight)
        )
        .scaledToFill()
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
    static let cardWidth: CGFloat = 210
    static let cardHeight: CGFloat = 140
}

#Preview {
    HStack {
        ListCollageCard(
            list: SwiftProfileList(
                id: 1,
                name: "Watchlist",
                itemCountLabel: "24 shows",
                posterUrls: ["a", "b", "c", "d"]
            ),
            onClick: {}
        )
        ListCollageCard(
            list: SwiftProfileList(
                id: 2,
                name: "Empty List",
                itemCountLabel: "0 shows",
                posterUrls: []
            ),
            onClick: {}
        )
    }
}
