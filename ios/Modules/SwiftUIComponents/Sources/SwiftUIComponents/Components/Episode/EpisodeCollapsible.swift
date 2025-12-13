import SwiftUI

public struct EpisodeCollapsible<Content: View>: View {
    @Theme private var theme

    private let title: String
    private let episodeCount: Int64
    private let watchProgress: CGFloat
    private let isCollapsed: Bool
    private let isSeasonWatched: Bool
    private let onCollapseClicked: () -> Void
    private let onWatchedStateClicked: () -> Void
    private let content: Content

    public init(
        title: String,
        episodeCount: Int64,
        watchProgress: CGFloat,
        isCollapsed: Bool,
        isSeasonWatched: Bool = false,
        onCollapseClicked: @escaping () -> Void,
        onWatchedStateClicked: @escaping () -> Void,
        @ViewBuilder content: () -> Content
    ) {
        self.title = title
        self.episodeCount = episodeCount
        self.watchProgress = watchProgress
        self.isCollapsed = isCollapsed
        self.isSeasonWatched = isSeasonWatched
        self.onCollapseClicked = onCollapseClicked
        self.onWatchedStateClicked = onWatchedStateClicked
        self.content = content()
    }

    public var body: some View {
        VStack(spacing: 0) {
            headerCard

            if !isCollapsed {
                content
                    .padding(.top, theme.spacing.small)
            }
        }
    }

    // MARK: - Subviews

    private var headerCard: some View {
        VStack(spacing: 0) {
            Spacer()

            HStack {
                collapseButton
                Spacer()
                episodeCountLabel
                watchedButton
            }
            .padding(.bottom, theme.spacing.xxSmall)

            Spacer()

            progressBar
        }
        .frame(maxWidth: .infinity, minHeight: DimensionConstants.frameHeight)
        .background(theme.colors.surface)
        .cornerRadius(theme.shapes.small)
        .padding(.horizontal, theme.spacing.medium)
    }

    private var collapseButton: some View {
        HStack {
            Image(systemName: isCollapsed ? "chevron.down" : "chevron.up")
                .aspectRatio(contentMode: .fit)
                .padding(.horizontal, theme.spacing.medium)

            Text(title)
                .textStyle(theme.typography.titleMedium)
        }
        .contentShape(Rectangle())
        .onTapGesture {
            withAnimation {
                onCollapseClicked()
            }
        }
    }

    private var episodeCountLabel: some View {
        Text("\(episodeCount)")
            .textStyle(theme.typography.bodyMedium)
            .padding(.trailing, theme.spacing.xSmall)
    }

    private var watchedButton: some View {
        Button(action: onWatchedStateClicked) {
            Image(systemName: "checkmark.circle.fill")
                .resizable()
                .frame(width: DimensionConstants.checkmarkSize, height: DimensionConstants.checkmarkSize)
                .foregroundColor(isSeasonWatched ? theme.colors.success : theme.colors.grey)
        }
        .buttonStyle(.plain)
        .padding(.trailing, theme.spacing.medium)
    }

    private var progressBar: some View {
        ProgressView(value: watchProgress, total: 1)
            .progressViewStyle(
                RoundedRectProgressViewStyle(progressIndicatorHeight: DimensionConstants.progressIndicatorHeight)
            )
    }
}

private enum DimensionConstants {
    static let frameHeight: CGFloat = 68
    static let progressIndicatorHeight: CGFloat = 4
    static let checkmarkSize: CGFloat = 28
}

#Preview {
    VStack {
        Spacer()

        EpisodeCollapsible(
            title: "Episodes",
            episodeCount: 25,
            watchProgress: 0.6,
            isCollapsed: false,
            onCollapseClicked: {},
            onWatchedStateClicked: {}
        ) {
            VStack {}
        }
        Spacer()
    }
    .themedPreview()
}
