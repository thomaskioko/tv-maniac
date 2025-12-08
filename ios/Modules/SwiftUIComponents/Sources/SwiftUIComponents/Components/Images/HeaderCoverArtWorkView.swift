//
//  HeaderCoverArtWorkView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SDWebImageSwiftUI
import SwiftUI

public struct HeaderCoverArtWorkView: View {
    @Theme private var theme

    private let imageUrl: String?
    private let posterHeight: CGFloat
    private let cornerRadius: CGFloat

    public init(
        imageUrl: String?,
        posterHeight: CGFloat,
        cornerRadius: CGFloat = 0
    ) {
        self.imageUrl = imageUrl
        self.posterHeight = posterHeight
        self.cornerRadius = cornerRadius
    }

    public var body: some View {
        if let imageUrl {
            WebImage(
                url: URL(string: imageUrl.transformedImageURL), options: .highPriority
            ) { image in
                image.resizable()
            } placeholder: {
                headerPosterPlaceholder
            }
            .aspectRatio(contentMode: .fill)
            .transition(.opacity)
            .frame(width: DimensionConstants.posterWidth, height: posterHeight)
        } else {
            headerPosterPlaceholder
        }
    }

    @ViewBuilder
    private var headerPosterPlaceholder: some View {
        ZStack {
            Rectangle().fill(.gray.gradient)
            VStack {
                Image(systemName: "popcorn.fill")
                    .textStyle(theme.typography.titleLarge)
                    .fontWidth(.expanded)
                    .foregroundColor(theme.colors.onPrimary.opacity(0.8))
                    .frame(width: 120, height: 120)
                    .padding()
            }
        }
        .frame(width: DimensionConstants.posterWidth, height: posterHeight)
        .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
        .shadow(radius: DimensionConstants.shadowRadius)
    }
}

private enum DimensionConstants {
    static let posterWidth: CGFloat = UIScreen.main.bounds.width
    static let shadowRadius: CGFloat = 2
    static let cornerRadius: CGFloat = 8
}

#Preview {
    VStack {
        HeaderCoverArtWorkView(
            imageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            posterHeight: 320
        )

        Spacer()
    }
}
