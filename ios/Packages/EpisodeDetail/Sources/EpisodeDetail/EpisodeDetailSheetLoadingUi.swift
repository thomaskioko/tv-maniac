import Components
import DesignSystem
import SwiftUI

/// Shimmer placeholder that mirrors `EpisodeDetailSheetContent` while the episode loads.
/// Keeps the sheet at a stable size so it presents once instead of flashing blank then content.
struct EpisodeDetailSheetLoadingUi: View {
    @Environment(\.appTheme) private var theme

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                ShimmerView(cornerRadius: 0)
                    .frame(height: 280)

                VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
                    ShimmerView()
                        .frame(width: 220, height: 28)

                    ShimmerView()
                        .frame(width: 140, height: 18)

                    ShimmerView()
                        .frame(width: 90, height: 16)

                    VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                        ShimmerView()
                            .frame(maxWidth: .infinity)
                            .frame(height: 14)
                        ShimmerView()
                            .frame(maxWidth: .infinity)
                            .frame(height: 14)
                        ShimmerView()
                            .frame(width: 200, height: 14)
                    }
                    .padding(.top, theme.spacing.xxSmall)
                }
                .padding(.horizontal, theme.spacing.medium)
                .padding(.top, theme.spacing.small)

                Divider()
                    .padding(.top, theme.spacing.medium)

                VStack(alignment: .leading, spacing: 0) {
                    ForEach(0 ..< 3, id: \.self) { _ in
                        actionRow
                    }
                }
            }
        }
        .scrollBounceBehavior(.basedOnSize)
        .background(.appSurface)
    }

    private var actionRow: some View {
        HStack(spacing: theme.spacing.medium) {
            ShimmerView(cornerRadius: theme.shapes.medium)
                .frame(width: 24, height: 24)

            ShimmerView()
                .frame(width: 160, height: 18)

            Spacer()
        }
        .padding(.horizontal, theme.spacing.medium)
        .padding(.vertical, theme.spacing.small)
    }
}

#Preview("Episode Detail Loading") {
    EpisodeDetailSheetLoadingUi()
        .appPreview()
}
