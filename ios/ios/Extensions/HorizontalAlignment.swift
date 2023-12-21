//
//  HorizontalAlignment.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

extension HorizontalAlignment {
    struct ViewAlignment: AlignmentID {
        static func defaultValue(in d: ViewDimensions) -> CGFloat {
            d[HorizontalAlignment.leading]
        }
    }
    static let view = HorizontalAlignment(ViewAlignment.self)
}
