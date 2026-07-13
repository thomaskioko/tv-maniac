import Components
import DesignSystem
import SwiftUI

struct SearchResultsShimmerView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    var itemCount: Int = 6

    var body: some View {
        let posterWidth = ImageDimens.posterWidthFixed(widthSizeClass)
        let posterHeight = posterWidth / ImageType.poster.aspect

        VStack(spacing: theme.spacing.small) {
            ForEach(0 ..< itemCount, id: \.self) { _ in
                HStack(alignment: .top, spacing: theme.spacing.xxxSmall) {
                    ShimmerView(cornerRadius: 0)
                        .frame(width: posterWidth, height: posterHeight)

                    VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
                        ShimmerView().frame(width: 140, height: 16)
                        ShimmerView().frame(width: 90, height: 12)
                        ShimmerView().frame(height: 12)
                        ShimmerView().frame(width: 200, height: 12)
                    }
                    .padding(.vertical, theme.spacing.xxxSmall)
                    .padding(.horizontal, theme.spacing.xSmall)

                    Spacer()
                }
                .frame(maxWidth: .infinity)
            }
        }
        .padding(.horizontal, theme.spacing.xSmall)
    }
}

#Preview {
    SearchResultsShimmerView()
        .padding()
}
