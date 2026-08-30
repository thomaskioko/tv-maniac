import DesignSystem
import SwiftUI
import WidgetKit

public struct UpNextWidgetView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widgetRenderingMode) private var renderingMode

    private let state: UpNextWidgetState
    private let family: WidgetFamily

    public init(state: UpNextWidgetState, family: WidgetFamily) {
        self.state = state
        self.family = family
    }

    public var body: some View {
        content
            .containerBackground(theme.colors.surface, for: .widget)
    }

    @ViewBuilder
    private var content: some View {
        switch state {
        case .placeholder:
            PlaceholderContent(rowCount: rowCount)
        case let .empty(message):
            EmptyContent(message: message)
        case let .content(items, lastUpdated):
            ContentRows(
                items: Array(items.prefix(rowCount)),
                lastUpdated: lastUpdated,
                showsPosterOnly: family == .systemSmall,
                renderingMode: renderingMode
            )
        }
    }

    private var rowCount: Int {
        switch family {
        case .systemSmall: 1
        case .systemMedium: 2
        default: 4
        }
    }
}

private struct ContentRows: View {
    @Environment(\.appTheme) private var theme

    let items: [UpNextItem]
    let lastUpdated: String?
    let showsPosterOnly: Bool
    let renderingMode: WidgetRenderingMode

    var body: some View {
        VStack(alignment: .leading, spacing: theme.spacing.small) {
            ForEach(items) { item in
                row(for: item)
            }

            if let lastUpdated {
                Text(lastUpdated)
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(theme.colors.onSurfaceVariant)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }

    @ViewBuilder
    private func row(for item: UpNextItem) -> some View {
        if showsPosterOnly {
            UpNextRow(item: item, renderingMode: renderingMode)
        } else if let destination = item.destination {
            Link(destination: destination) {
                UpNextRow(item: item, renderingMode: renderingMode)
            }
        } else {
            UpNextRow(item: item, renderingMode: renderingMode)
        }
    }
}

private struct UpNextRow: View {
    @Environment(\.appTheme) private var theme

    let item: UpNextItem
    let renderingMode: WidgetRenderingMode

    var body: some View {
        HStack(spacing: theme.spacing.small) {
            PosterView(image: item.poster, showName: item.showName, renderingMode: renderingMode)

            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                Text(item.showName)
                    .textStyle(theme.typography.labelMedium)
                    .foregroundStyle(theme.colors.onSurface)
                    .lineLimit(1)

                Text(item.seasonEpisodeLabel)
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(theme.colors.onSurfaceVariant)
                    .lineLimit(1)

                Text(item.episodeName)
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(theme.colors.onSurfaceVariant)
                    .lineLimit(1)
            }

            Spacer(minLength: 0)
        }
    }
}

private struct PosterView: View {
    @Environment(\.appTheme) private var theme

    let image: UIImage?
    let showName: String
    let renderingMode: WidgetRenderingMode

    var body: some View {
        Group {
            if let image {
                Image(uiImage: image)
                    .resizable()
                    .widgetAccentedRenderingMode(.fullColor)
                    .aspectRatio(contentMode: .fill)
            } else {
                Rectangle()
                    .fill(theme.colors.surfaceVariant)
            }
        }
        .frame(width: posterWidth, height: posterHeight)
        .clipShape(ContainerRelativeShape())
        .accessibilityLabel(showName)
    }

    private var posterWidth: CGFloat {
        36
    }

    private var posterHeight: CGFloat {
        54
    }
}

private struct EmptyContent: View {
    @Environment(\.appTheme) private var theme

    let message: String

    var body: some View {
        Text(message)
            .textStyle(theme.typography.labelMedium)
            .foregroundStyle(theme.colors.onSurfaceVariant)
            .multilineTextAlignment(.center)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct PlaceholderContent: View {
    @Environment(\.appTheme) private var theme

    let rowCount: Int

    var body: some View {
        VStack(alignment: .leading, spacing: theme.spacing.small) {
            ForEach(0 ..< rowCount, id: \.self) { _ in
                HStack(spacing: theme.spacing.small) {
                    Rectangle()
                        .fill(theme.colors.surfaceVariant)
                        .frame(width: 36, height: 54)
                        .clipShape(ContainerRelativeShape())

                    VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                        shape(widthRatio: 0.7)
                        shape(widthRatio: 0.4)
                        shape(widthRatio: 0.55)
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }

    private func shape(widthRatio: CGFloat) -> some View {
        GeometryReader { proxy in
            Capsule()
                .fill(theme.colors.surfaceVariant)
                .frame(width: proxy.size.width * widthRatio, height: 8)
        }
        .frame(height: 8)
    }
}
