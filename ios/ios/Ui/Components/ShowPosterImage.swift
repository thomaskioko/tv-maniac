import SwiftUI

struct ShowPosterImage: View {

    let posterSize: PosterStyle.Size
    let imageUrl: String

    var body: some View {

        AsyncImage(url: URL(string: imageUrl)) { image in
            image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .cornerRadius(CGFloat(5))
        } placeholder: {
            ProgressView()
                    .progressViewStyle(.circular)
                    .tint(Color.accent_color)

            Rectangle()
                    .foregroundColor(.gray)
                    .posterStyle(loaded: false, size: posterSize)
        }
                .frame(width: posterSize.width(), height: posterSize.height())

    }
}
