import SwiftUI
import Kingfisher

struct ShowPosterImage: View {
    
    let posterSize: PosterStyle.Size
    let imageUrl: String?
    let showTitle: String
    
    var body: some View {
        
        let processor: ImageProcessor = DownsamplingImageProcessor(size: CGSize(width: posterSize.width(), height: posterSize.height())) |> RoundCornerImageProcessor(cornerRadius: 5)
        
        if let posterUrl = imageUrl {
            KFImage.url(URL(string: posterUrl))
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
                        .frame(width: posterSize.width(), height: posterSize.height())
                        .posterStyle(loaded: false, size: posterSize)
                }
                .aspectRatio(contentMode: .fill)
                .cornerRadius(5)
                .frame(width: posterSize.width(), height: posterSize.height())
        } else {
            ZStack {
                
                Text(showTitle)
                    .captionFont(size: 14)
                    .lineLimit(1)
                    .foregroundColor(Color.text_color_bg)
                    .frame(width: posterSize.width(), height: posterSize.height())
                    .cornerRadius(10)
                
                Rectangle()
                    .foregroundColor(Color.accent)
                    .frame(width: posterSize.width(), height: posterSize.height())
                    .posterStyle(loaded: false, size: posterSize)
            }
        }
    }
}
