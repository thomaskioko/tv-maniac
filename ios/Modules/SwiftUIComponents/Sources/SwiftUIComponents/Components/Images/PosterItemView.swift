//
//  PosterItemView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SDWebImageSwiftUI
import SwiftUI

public struct PosterItemView: View {
    private let title: String?
    private let posterUrl: String?
    private let libraryImageOverlay: String
    private let isInLibrary: Bool
    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let posterRadius: CGFloat

    public init(
        title: String?,
        posterUrl: String?,
        libraryImageOverlay: String = "square.stack.fill",
        isInLibrary: Bool = false,
        posterWidth: CGFloat = 120,
        posterHeight: CGFloat = 180,
        posterRadius: CGFloat = 4
    ) {
        self.title = title
        self.posterUrl = posterUrl
        self.libraryImageOverlay = libraryImageOverlay
        self.isInLibrary = isInLibrary
        self.posterWidth = posterWidth
        self.posterHeight = posterHeight
        self.posterRadius = posterRadius
    }

    public var body: some View {
        if let posterUrl {
            WebImage(
                url: URL(string: posterUrl.transformedImageURL),
                options: [
                    .retryFailed,
                    .highPriority,
                    .scaleDownLargeImages,
                ],
                context: [
                    .imageThumbnailPixelSize: CGSize(
                        width: posterWidth * 2,
                        height: posterHeight * 2
                    ),
                    .imageForceDecodePolicy: SDImageForceDecodePolicy.never.rawValue,
                ]
            ) { image in
                image
                    .resizable()
                    .scaledToFill()
            } placeholder: {
                PosterPlaceholder(
                    title: title,
                    posterWidth: posterWidth,
                    posterHeight: posterHeight
                )
            }
            .indicator(.activity)
            .transition(.opacity)
            .scaledToFill()
            .clipShape(RoundedRectangle(cornerRadius: posterRadius, style: .continuous))
            .frame(width: posterWidth, height: posterHeight)
            .overlay {
                if isInLibrary {
                    LibraryOverlay(libraryImageOverlay: libraryImageOverlay)
                }
            }
        } else {
            PosterPlaceholder(
                title: title,
                posterWidth: posterWidth,
                posterHeight: posterHeight,
                posterRadius: posterRadius
            )
        }
    }
}

@ViewBuilder
private func LibraryOverlay(libraryImageOverlay: String) -> some View {
    VStack {
        Spacer()
        HStack {
            Spacer()
            Image(systemName: libraryImageOverlay)
                .imageScale(.medium)
                .foregroundColor(.white.opacity(0.9))
                .padding([.vertical])
                .padding(.trailing, 16)
                .font(.caption)
        }
        .background {
            Color.black.opacity(0.6)
                .mask {
                    LinearGradient(
                        colors: [
                            Color.black,
                            Color.black.opacity(0.924),
                            Color.black.opacity(0.707),
                            Color.black.opacity(0.383),
                            Color.black.opacity(0),
                        ],
                        startPoint: .bottom,
                        endPoint: .top
                    )
                }
        }
    }
}

private enum DimensionConstants {
    static let shadowRadius: CGFloat = 2
}

#Preview {
    VStack {
        PosterItemView(
            title: "Arcane",
            posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            isInLibrary: true,
            posterWidth: 160,
            posterHeight: 240
        )

        PosterItemView(
            title: "Arcane",
            posterUrl: nil,
            isInLibrary: true,
            posterWidth: 160,
            posterHeight: 240
        )
    }
}
