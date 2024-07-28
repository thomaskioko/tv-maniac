//
//  Geometry_Extensions.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/27/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

extension GeometryProxy {
    var blurRadius: CGFloat {
        let height = self.size.height
        let blur = (height - max(self.frame(in: .global).maxY, 0)) / height
        return blur * 6
    }

    func headerHeight(scrollOffset: CGFloat) -> CGFloat {
        let imageHeight = self.size.height
        if scrollOffset > 0 {
            return imageHeight + scrollOffset
        }
        return imageHeight
    }

    func headerOffset(scrollOffset: CGFloat, imageHeight: CGFloat, collapsedImageHeight: CGFloat) -> CGFloat {
        let sizeOffScreen = imageHeight - collapsedImageHeight

        if scrollOffset < -sizeOffScreen {
            let imageOffset = abs(min(-sizeOffScreen, scrollOffset))
            return imageOffset - sizeOffScreen
        }

        if scrollOffset > 0 {
            return -scrollOffset
        }

        return 0
    }

    func titleOpacity(scrollOffset: CGFloat, imageHeight: CGFloat, collapsedImageHeight: CGFloat) -> CGFloat {
        let progress = -scrollOffset / (imageHeight - collapsedImageHeight)
        return min(1, max(0, progress))
    }
}
