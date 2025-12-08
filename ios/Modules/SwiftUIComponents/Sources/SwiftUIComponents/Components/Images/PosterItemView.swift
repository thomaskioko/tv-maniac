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
    @Theme private var theme

    private let title: String?
    private let posterUrl: String?
    private let libraryImageOverlay: String
    private let isInLibrary: Bool
    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let posterRadius: CGFloat?

    public init(
        title: String?,
        posterUrl: String?,
        libraryImageOverlay: String = "square.stack.fill",
        isInLibrary: Bool = false,
        posterWidth: CGFloat = 120,
        posterHeight: CGFloat = 180,
        posterRadius: CGFloat? = nil
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
        let resolvedRadius = posterRadius ?? theme.shapes.small

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
            .clipShape(RoundedRectangle(cornerRadius: resolvedRadius, style: .continuous))
            .frame(width: posterWidth, height: posterHeight)
            .overlay {
                if isInLibrary {
                    LibraryOverlay(libraryImageOverlay: libraryImageOverlay, theme: theme)
                }
            }
        } else {
            PosterPlaceholder(
                title: title,
                posterWidth: posterWidth,
                posterHeight: posterHeight,
                posterRadius: resolvedRadius
            )
        }
    }
}

@ViewBuilder
private func LibraryOverlay(libraryImageOverlay: String, theme: TvManiacTheme) -> some View {
    VStack {
        Spacer()
        HStack {
            Spacer()
            Image(systemName: libraryImageOverlay)
                .imageScale(.medium)
                .foregroundColor(theme.colors.onPrimary.opacity(0.9))
                .padding([.vertical])
                .padding(.trailing, theme.spacing.medium)
                .textStyle(theme.typography.labelSmall)
        }
        .background {
            theme.colors.imageGradient()
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
