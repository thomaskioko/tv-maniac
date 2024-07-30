//
//  Geometry_Extensions.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/27/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

extension GeometryProxy {

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
