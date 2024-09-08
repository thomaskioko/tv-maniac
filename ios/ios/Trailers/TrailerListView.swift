//
//  TrailerListView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac

struct TrailerListView: View {
    var trailers: [Trailer]
    var openInYouTube: Bool

    var body: some View {
        if !trailers.isEmpty {
            VStack {
                ChevronTitle(title: "Trailers")

                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack {
                        ForEach(trailers, id: \.self) { trailer in
                            TrailerItemView(
                                openInYouTube: openInYouTube,
                                key: trailer.key,
                                name: trailer.name,
                                thumbnailUrl: trailer.youtubeThumbnailUrl
                            )
                            .padding(.horizontal, 4)
                            .padding(.leading, trailer.key == self.trailers.first?.key ? 16 : 0)
                            .padding(.trailing, trailer.key == self.trailers.last?.key ? 16 : 0)
                            .padding(.top, 4)
                        }
                    }
                }
            }
        }
    }
}
