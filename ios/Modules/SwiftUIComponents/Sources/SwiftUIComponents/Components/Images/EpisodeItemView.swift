//
//  EpisodeItemView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SDWebImageSwiftUI
import SwiftUI

public struct EpisodeItemView: View {
    private let imageUrl: String?
    private let episodeTitle: String
    private let episodeOverView: String
    private let episodeWidth: CGFloat
    private let episodeHeight: CGFloat
    private let shadowRadius: CGFloat
    private let cornerRadius: CGFloat

    public init(
        imageUrl: String?,
        episodeTitle: String,
        episodeOverView: String,
        episodeWidth: CGFloat = 120,
        episodeHeight: CGFloat = 140,
        shadowRadius: CGFloat = 2.5,
        cornerRadius: CGFloat = 2
    ) {
        self.imageUrl = imageUrl
        self.episodeTitle = episodeTitle
        self.episodeOverView = episodeOverView
        self.episodeWidth = episodeWidth
        self.episodeHeight = episodeHeight
        self.shadowRadius = shadowRadius
        self.cornerRadius = cornerRadius
    }

    public var body: some View {
        HStack {
            PosterItemView(
                title: episodeTitle,
                posterUrl: imageUrl,
                posterWidth: episodeWidth,
                posterHeight: episodeHeight
            )

            VStack {
                Text(episodeTitle)
                    .font(.title3)
                    .fontWeight(.semibold)
                    .lineLimit(1)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.bottom, 0.5)
                    .padding(.top, 16)

                Text(episodeOverView)
                    .font(.callout)
                    .padding([.top], 2)
                    .lineLimit(4)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)

                Spacer()
            }
            .padding(.horizontal, 8)
        }
        .frame(height: episodeHeight)
        .background(Color.content_background)
        .cornerRadius(4)
        .padding(.horizontal)
    }

    private var episodePlaceholder: some View {
        ZStack {
            ZStack {
                Rectangle().fill(.gray.gradient)
                Image(systemName: "popcorn.fill")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 50, height: 50, alignment: .center)
                    .foregroundColor(.white)
            }
            .frame(width: episodeWidth,
                   height: episodeHeight)
            .clipShape(
                RoundedRectangle(
                    cornerRadius: cornerRadius,
                    style: .continuous
                )
            )
            .shadow(radius: shadowRadius)
        }
    }
}

#Preview {
    EpisodeItemView(
        imageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
        episodeTitle: "E01 • Glorious Purpose",
        episodeOverView: "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority."
    )
}
