import SwiftUI
import Kingfisher

struct ShowPosterImage: View {

    let posterSize: PosterStyle.Size
    let imageUrl: String

    var body: some View {

        let processor: ImageProcessor = DownsamplingImageProcessor(size: CGSize(width: posterSize.width(), height: posterSize.height())) |> RoundCornerImageProcessor(cornerRadius: 5)

        KFImage.url(URL(string: imageUrl))
                .resizable()
                .loadDiskFileSynchronously()
                .cacheMemoryOnly()
                .fade(duration: 0.25)
                .setProcessor(processor)
                .placeholder {
                    ProgressView()
                            .progressViewStyle(.circular)
                            .tint(Color.accent_color)

                    Rectangle()
                            .foregroundColor(.gray)
                            .posterStyle(loaded: false, size: posterSize)
                }
                .aspectRatio(contentMode: .fill)
                .cornerRadius(5)
                .frame(width: posterSize.width(), height: posterSize.height())
    }
}
