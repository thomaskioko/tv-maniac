//
//  FancyToast.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.07.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct Toast: Equatable {
    var type: ToastStyle
    var title: String
    var message: String
    var duration: Double = 3.5
}
