import SwiftUI
import Kingfisher

struct ShowPosterImage: View {
	
	let processor: ImageProcessor
	let posterSize: PosterStyle.Size
	let imageUrl: String
	
	
	var body: some View {

		KFImage.url(URL(string: imageUrl))
			.resizable()
			.loadDiskFileSynchronously()
			.cacheMemoryOnly()
			.fade(duration: 0.25)
			.setProcessor(processor)
			.placeholder {
				Rectangle()
					.foregroundColor(.gray)
					.posterStyle(loaded: false, size: posterSize)
			}
	}
}
