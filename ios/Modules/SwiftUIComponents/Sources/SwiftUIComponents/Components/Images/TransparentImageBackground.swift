import SwiftUI
import UIKit

public struct TransparentImageBackground: View {
    @Theme private var theme

    private let imageUrl: String?

    public init(imageUrl: String?) {
        self.imageUrl = imageUrl
    }

    public var body: some View {
        LazyResizableImage(
            url: imageUrl,
            size: CGSize(
                width: UIScreen.main.bounds.width,
                height: UIScreen.main.bounds.height
            )
        ) { state in
            if let image = state.image {
                image.resizable()
            } else {
                Rectangle()
                    .fill(theme.colors.background)
                    .ignoresSafeArea()
                    .padding(.zero)
            }
        }
        .aspectRatio(contentMode: .fill)
        .ignoresSafeArea()
        .padding(.zero)
    }
}

#Preview {
    TransparentImageBackground(
        imageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg"
    )
}
