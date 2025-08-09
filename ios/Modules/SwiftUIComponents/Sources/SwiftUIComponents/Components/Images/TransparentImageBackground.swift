//
//  TransparentImageBackground.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SDWebImageSwiftUI
import SwiftUI

public struct TransparentImageBackground: View {
    private let imageUrl: String?

    public init(imageUrl: String?) {
        self.imageUrl = imageUrl
    }

    public var body: some View {
        if let imageUrl {
            ZStack {
                WebImage(
                    url: URL(string: imageUrl.transformedImageURL)
                ) { image in
                    image.resizable()
                } placeholder: {
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

#Preview {
    TransparentImageBackground(
        imageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg"
    )
}
