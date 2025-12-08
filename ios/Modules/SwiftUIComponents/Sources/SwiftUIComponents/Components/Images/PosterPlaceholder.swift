import SwiftUI

public struct PosterPlaceholder: View {
    @Theme private var theme

    private let title: String?
    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let posterRadius: CGFloat?
    private let shadowRadius: CGFloat

    public init(
        title: String? = nil,
        posterWidth: CGFloat = 160,
        posterHeight: CGFloat = 240,
        posterRadius: CGFloat? = nil,
        shadowRadius: CGFloat = 8
    ) {
        self.title = title
        self.posterWidth = posterWidth
        self.posterHeight = posterHeight
        self.posterRadius = posterRadius
        self.shadowRadius = shadowRadius
    }

    public var body: some View {
        ZStack {
            Rectangle().fill(.gray.gradient)
            VStack {
                Image(systemName: "popcorn.fill")
                    .textStyle(theme.typography.titleLarge)
                    .fontWidth(.expanded)
                    .foregroundColor(theme.colors.onPrimary.opacity(0.8))
                    .padding(theme.spacing.medium)

                if let title {
                    Text(title)
                        .textStyle(theme.typography.bodyMedium)
                        .foregroundColor(theme.colors.onPrimary.opacity(0.8))
                        .lineLimit(2)
                        .multilineTextAlignment(.center)
                        .padding(.bottom, theme.spacing.medium)
                        .padding(.horizontal, theme.spacing.xxSmall)
                }
            }
        }
        .frame(width: posterWidth, height: posterHeight)
        .clipShape(RoundedRectangle(cornerRadius: posterRadius ?? theme.shapes.small, style: .continuous))
        .shadow(radius: shadowRadius)
    }
}

#Preview {
    VStack {
        PosterPlaceholder(title: "Arcane")

        PosterPlaceholder()
    }
}
