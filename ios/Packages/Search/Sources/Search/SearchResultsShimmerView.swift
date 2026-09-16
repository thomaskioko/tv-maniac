import Components
import DesignSystem
import SwiftUI

struct SearchResultsShimmerView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    var itemCount: Int = 9

    var body: some View {
        LazyVGrid(
            columns: ImageDimens.posterGridColumns(widthSizeClass, spacing: theme.spacing.small),
            spacing: theme.spacing.small
        ) {
            ForEach(0 ..< itemCount, id: \.self) { _ in
                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    ShimmerView(cornerRadius: 0)
                        .aspectRatio(ImageDimens.posterAspect, contentMode: .fill)
                        .frame(maxWidth: .infinity)

                    ShimmerView()
                        .frame(width: 60, height: 12)
                }
            }
        }
        .padding(.horizontal, theme.spacing.medium)
    }
}

#Preview {
    SearchResultsShimmerView()
        .padding(.vertical)
}
