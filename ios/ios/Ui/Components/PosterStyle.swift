import SwiftUI

struct PosterStyle: ViewModifier {
	enum Size {
		case small, medium, big, tv, max
		
		func width() -> CGFloat {
			switch self {
			case .small: return 60
			case .medium: return 120
			case .big: return 260
			case .tv: return 310
			case .max: return UIScreen.main.bounds.width
			}
		}
		func height() -> CGFloat {
			switch self {
			case .small: return 80
			case .medium: return 180
			case .big: return 460
			case .tv: return 600
			case .max: return 720
			}
		}
	}
	
	let loaded: Bool
	let size: Size
	
	func body(content: Content) -> some View {
		content
				.frame(width: size.width(), height: size.height())
				.cornerRadius(5)
				.opacity(loaded ? 1 : 0.1)
				.shadow(radius: 8)
	}
}

extension View {
	func posterStyle(loaded: Bool, size: PosterStyle.Size) -> some View {
		ModifiedContent(content: self, modifier: PosterStyle(loaded: loaded, size: size))
	}
}
