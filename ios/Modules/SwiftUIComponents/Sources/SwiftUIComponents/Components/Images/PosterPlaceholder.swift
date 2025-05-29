import SwiftUI

public struct PosterPlaceholder: View {
    private let title: String?
    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let posterRadius: CGFloat
    private let shadowRadius: CGFloat

    public init(
        title: String? = nil,
        posterWidth: CGFloat = 160,
        posterHeight: CGFloat = 240,
        posterRadius: CGFloat = 4,
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
                    .font(.title)
                    .fontWidth(.expanded)
                    .foregroundColor(.white.opacity(0.8))
                    .padding()

                if let title {
                    Text(title)
                        .font(.callout)
                        .foregroundColor(.white.opacity(0.8))
                        .lineLimit(2)
                        .multilineTextAlignment(.center)
                        .padding(.bottom)
                        .padding(.horizontal, 4)
                }
            }
        }
        .frame(width: posterWidth, height: posterHeight)
        .clipShape(RoundedRectangle(cornerRadius: posterRadius, style: .continuous))
        .shadow(radius: shadowRadius)
    }
}

#Preview {
    VStack {
        PosterPlaceholder(title: "Arcane")

        PosterPlaceholder()
    }
}
