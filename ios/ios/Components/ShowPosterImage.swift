import SwiftUI
import Kingfisher

struct ShowPosterImage: View {
    
    @Namespace var animation
    @State private var show: Bool = false
    
    let posterSize: PosterStyle.Size
    let imageUrl: String?
    let showTitle: String
    let showId: Int64
    var onClick : () -> Void
    
    
    var body: some View {
        
        let processor: ImageProcessor = DownsamplingImageProcessor(size: CGSize(width: posterSize.width(), height: posterSize.height())) |> RoundCornerImageProcessor(cornerRadius: 5)
        
        if let posterUrl = imageUrl {
            KFImage.url(URL(string: posterUrl))
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
                .resizable()
                .setProcessor(ResizingImageProcessor(referenceSize: CGSize(width: posterSize.width() * scale, height: posterSize.height() * scale), mode: .aspectFit))
                .aspectRatio(contentMode: .fill)
                .frame(width: posterSize.width(), height: posterSize.height())
                .cornerRadius(5)
                .shadow(radius: 10)
                .matchedGeometryEffect(id: showId, in: animation)
                .onTapGesture { onClick()}
        } else {
            ZStack {
                Text(showTitle)
                    .padding(.trailing, 16)
                    .padding(.leading, 16)
                    .captionFont(size: 14)
                    .lineLimit(2)
                    .multilineTextAlignment(.center)
                    .foregroundColor(Color.text_color_bg)
                    .frame(width: posterSize.width(), height: posterSize.height())
                    .cornerRadius(10)
                
                
                Rectangle()
                    .foregroundColor(Color.accent)
                    .frame(width: posterSize.width(), height: posterSize.height())
                    .posterStyle(loaded: false, size: posterSize)
                    .cornerRadius(5)
                    .shadow(radius: 10)
                    .matchedGeometryEffect(id: showId, in: animation)
                    .onTapGesture { onClick()}
            }
        }
    }
    
    private var scale: CGFloat {
            UIScreen.main.scale
        }
}
