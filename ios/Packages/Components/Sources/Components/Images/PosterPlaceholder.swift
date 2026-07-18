import DesignSystem
import SwiftUI

public struct PosterPlaceholder: View {
    @Environment(\.appTheme) private var theme

    private let title: String?
    private let icon: String
    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let posterRadius: CGFloat?
    private let shadowToken: TvManiacShadowToken?

    public init(
        title: String? = nil,
        icon: String = "popcorn.fill",
        posterWidth: CGFloat = 120,
        posterHeight: CGFloat = 180,
        posterRadius: CGFloat? = nil,
        shadowToken: TvManiacShadowToken? = nil
    ) {
        self.title = title
        self.icon = icon
        self.posterWidth = posterWidth
        self.posterHeight = posterHeight
        self.posterRadius = posterRadius
        self.shadowToken = shadowToken
    }

    public var body: some View {
        ZStack {
            Rectangle().fill(theme.colors.surfaceVariant.gradient)
            VStack {
                Image(systemName: icon)
                    .textStyle(theme.typography.titleLarge)
                    .fontWidth(.expanded)
                    .foregroundStyle(.appOnSurfaceVariant.opacity(0.8))
                    .padding(theme.spacing.medium)

                if let title {
                    Text(title)
                        .textStyle(theme.typography.bodyMedium)
                        .foregroundStyle(.appOnSurfaceVariant.opacity(0.8))
                        .lineLimit(2)
                        .multilineTextAlignment(.center)
                        .padding(.bottom, theme.spacing.medium)
                        .padding(.horizontal, theme.spacing.xxSmall)
                }
            }
        }
        .frame(width: posterWidth, height: posterHeight)
        .clipShape(RoundedRectangle(cornerRadius: posterRadius ?? 0, style: .continuous))
        .appShadow(shadowToken ?? .none)
    }
}

#Preview {
    VStack {
        PosterPlaceholder(title: "Arcane")

        PosterPlaceholder()

        PosterPlaceholder(icon: "person", posterWidth: 120, posterHeight: 160)

        PosterPlaceholder(icon: "tv", posterWidth: 80, posterHeight: 70)
    }
}
