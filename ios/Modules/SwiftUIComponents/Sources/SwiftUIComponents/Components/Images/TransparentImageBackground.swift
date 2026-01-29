//
//  TransparentImageBackground.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct TransparentImageBackground: View {
    @Theme private var theme

    private let imageUrl: String?

    public init(imageUrl: String?) {
        self.imageUrl = imageUrl
    }

    public var body: some View {
        CachedAsyncImage(url: imageUrl) {
            Rectangle()
                .fill(theme.colors.background)
                .ignoresSafeArea()
                .padding(.zero)
        }
        .aspectRatio(contentMode: .fill)
        .ignoresSafeArea()
        .padding(.zero)
    }
}

#Preview {
    TransparentImageBackground(
        imageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg"
    )
}
