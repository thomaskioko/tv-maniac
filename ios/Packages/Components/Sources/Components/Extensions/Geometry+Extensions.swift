import DesignSystem
import SwiftUI

public extension GeometryProxy {
    func getTitleOpacity(geometry: GeometryProxy, imageHeight: CGFloat, collapsedImageHeight: CGFloat) -> Double {
        let progress = -geometry.frame(in: .global).minY / (imageHeight - collapsedImageHeight)
        return Double(min(1, max(0, progress)))
    }

    func getScrollOffset(_ geometry: GeometryProxy) -> CGFloat {
        geometry.frame(in: .global).minY
    }

    func getHeightForHeaderImage(_ geometry: GeometryProxy) -> CGFloat {
        let offset = getScrollOffset(geometry)
        let imageHeight = geometry.size.height

        if offset > 0 {
            return imageHeight + offset
        }

        return imageHeight
    }
}
