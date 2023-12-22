//
//  TransparentImageBackground.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SDWebImageSwiftUI

struct TransparentImageBackground: View {
    var imageUrl: String?
    
    var body: some View {
        if let imageUrl = imageUrl {
            ZStack {
                WebImage(url: URL(string: imageUrl))
                    .placeholder {
                        Rectangle()
                            .fill(.background)
                            .ignoresSafeArea()
                            .padding(.zero)
                    }
                    .aspectRatio(contentMode: .fill)
                    .ignoresSafeArea()
                    .padding(.zero)
                    .transition(.opacity)
            }
        }
    }
}

