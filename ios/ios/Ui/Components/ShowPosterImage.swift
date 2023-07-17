import SwiftUI
import Kingfisher

struct ShowPosterImage: View {
    
    @Namespace var animation
    @State private var show: Bool = false
    @State private var selectedShow: Int64 = -1
    
    let posterSize: PosterStyle.Size
    let imageUrl: String?
    let showTitle: String
    let showId: Int64

    
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
                .frame(width: posterSize.width(), height: posterSize.height())
                .cornerRadius(5)
                .shadow(radius: 10)
                .matchedGeometryEffect(id: showId, in: animation)
                .onTapGesture {
                    /// Adding Animation
                    withAnimation(.interactiveSpring(response: 0.6, dampingFraction: 0.8, blendDuration: 0.8)) {
                        selectedShow = showId
                        show.toggle()
                    }
                }
                .detailScreenCover(show: $show) {
                    /// Detail View
                    ShowDetailView(showId: $selectedShow, animationID: animation)
                }
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
                    .onTapGesture {
                        /// Adding Animation
                        withAnimation(.interactiveSpring(response: 0.6, dampingFraction: 0.8, blendDuration: 0.8)) {
                            selectedShow = showId
                            show.toggle()
                        }
                    }
                    .detailScreenCover(show: $show) {
                        /// Detail View
                        ShowDetailView(showId: $selectedShow, animationID: animation)
                    }
            }
        }
    }
}
