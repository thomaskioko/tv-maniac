import SwiftUI

public struct EpisodeCollapsible<Content: View>: View {
    @Theme private var theme
    @State private var collapsed: Bool = false

    private let episodeCount: Int64
    private let watchProgress: CGFloat
    private let isCollapsed: Bool
    private let onCollapseClicked: () -> Void
    private let onWatchedStateClicked: () -> Void
    private let content: Content

    public init(
        episodeCount: Int64,
        watchProgress: CGFloat,
        isCollapsed: Bool,
        onCollapseClicked: @escaping () -> Void,
        onWatchedStateClicked: @escaping () -> Void,
        @ViewBuilder content: () -> Content
    ) {
        self.episodeCount = episodeCount
        self.watchProgress = watchProgress
        self.isCollapsed = isCollapsed
        self.onCollapseClicked = onCollapseClicked
        self.onWatchedStateClicked = onWatchedStateClicked
        self.content = content()
    }

    public var body: some View {
        VStack {
            ZStack {
                VStack {
                    Spacer()

                    HStack {
                        HStack {
                            Image(systemName: collapsed ? "chevron.down" : "chevron.up")
                                .aspectRatio(contentMode: .fit)
                                .padding(.horizontal, theme.spacing.medium)

                            Text("Episodes")
                                .textStyle(theme.typography.titleMedium)

                            Spacer()
                        }
                        .contentShape(Rectangle())
                        .onTapGesture {
                            withAnimation {
                                onCollapseClicked()
                                collapsed.toggle()
                            }
                        }

                        Text("\(episodeCount)")
                            .textStyle(theme.typography.bodyMedium)
                            .alignmentGuide(.view) { d in d[HorizontalAlignment.center] }
                            .padding(.trailing, theme.spacing.xSmall)

                        Image(systemName: "checkmark.circle")
                            .resizable()
                            .frame(width: 28.0, height: 28.0)
                            .foregroundColor(theme.colors.onBackground)
                            .alignmentGuide(.view) { d in d[HorizontalAlignment.trailing] }
                            .padding(.trailing, theme.spacing.medium)
                            .onTapGesture { onWatchedStateClicked() }
                    }
                    .padding(.bottom, theme.spacing.xxSmall)

                    Spacer()

                    ProgressView(value: watchProgress, total: 1)
                        .progressViewStyle(
                            RoundedRectProgressViewStyle(progressIndicatorHeight: DimensionConstants
                                .progressIndicatorHeight)
                        )
                }
                .frame(height: DimensionConstants.frameHeight)
                .background(theme.colors.surface)
                .cornerRadius(theme.shapes.small)
            }
            .padding(.horizontal, theme.spacing.medium)

            VStack {
                content
            }
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: collapsed ? 0 : .none)
            .clipped()
            .animation(
                Animation.easeInOut(duration: 0), value: collapsed
            )
            .transition(.slide)
        }
        .onAppear {
            collapsed = isCollapsed
        }
    }
}

private enum DimensionConstants {
    static let frameHeight: CGFloat = 68
    static let progressIndicatorHeight: CGFloat = 4
}

#Preview {
    VStack {
        Spacer()

        EpisodeCollapsible(
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
