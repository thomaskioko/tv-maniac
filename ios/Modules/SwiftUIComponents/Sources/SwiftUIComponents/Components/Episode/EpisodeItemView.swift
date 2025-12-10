//
//  EpisodeItemView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct EpisodeItemView: View {
    @Theme private var theme

    private let imageUrl: String?
    private let episodeTitle: String
    private let episodeOverView: String
    private let episodeWidth: CGFloat
    private let episodeHeight: CGFloat
    private let shadowRadius: CGFloat
    private let cornerRadius: CGFloat
    private let posterRadius: CGFloat

    public init(
        imageUrl: String?,
        episodeTitle: String,
        episodeOverView: String,
        episodeWidth: CGFloat = Constants.defaultEpisodeWidth,
        episodeHeight: CGFloat = Constants.defaultEpisodeHeight,
        shadowRadius: CGFloat = Constants.defaultShadowRadius,
        cornerRadius: CGFloat = Constants.defaultCornerRadius,
        posterRadius: CGFloat = Constants.defaultPosterRadius
    ) {
        self.imageUrl = imageUrl
        self.episodeTitle = episodeTitle
        self.episodeOverView = episodeOverView
        self.episodeWidth = episodeWidth
        self.episodeHeight = episodeHeight
        self.shadowRadius = shadowRadius
        self.cornerRadius = cornerRadius
        self.posterRadius = posterRadius
    }

    public var body: some View {
        HStack(spacing: 0) {
            PosterItemView(
                title: nil,
                posterUrl: imageUrl,
                posterWidth: episodeWidth,
                posterHeight: episodeHeight,
                posterRadius: posterRadius
            )
            .clipped()

            episodeDetails
        }
        .frame(maxWidth: .infinity)
        .frame(height: episodeHeight)
        .background(theme.colors.surface)
        .cornerRadius(cornerRadius)
        .padding(.horizontal, theme.spacing.medium)
    }

    private var episodeDetails: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            Text(episodeTitle)
                .textStyle(theme.typography.titleMedium)
                .lineLimit(1)
                .padding(.top, theme.spacing.xSmall)

            Text(episodeOverView)
                .textStyle(theme.typography.bodySmall)
                .foregroundColor(theme.colors.onSurface)
                .lineSpacing(theme.spacing.xxSmall)
                .lineLimit(4)
                .multilineTextAlignment(.leading)
                .padding(.top, theme.spacing.xxxSmall)

            Spacer()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, theme.spacing.medium)
        .padding(.horizontal, theme.spacing.xSmall)
    }

    private var episodePlaceholder: some View {
        ZStack {
            ZStack {
                Rectangle().fill(.gray.gradient)
                Image(systemName: "popcorn.fill")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 50, height: 50, alignment: .center)
                    .foregroundColor(theme.colors.onPrimary)
            }
            .frame(
                width: episodeWidth,
                height: episodeHeight
            )
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

public enum Constants {
    public static let defaultEpisodeWidth: CGFloat = 120
    public static let defaultEpisodeHeight: CGFloat = 140
    public static let defaultShadowRadius: CGFloat = 2.5
    public static let defaultCornerRadius: CGFloat = 2
    public static let defaultPosterRadius: CGFloat = 0
    public static let horizontalPadding: CGFloat = 16
    public static let verticalPadding: CGFloat = 16
    public static let titleLineLimit: Int = 1
    public static let overviewLineLimit: Int = 4
}

#Preview {
    VStack {
        EpisodeItemView(
            imageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            episodeTitle: "E01 • Glorious Purpose",
            episodeOverView: "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority."
        )

        EpisodeItemView(
            imageUrl: nil,
            episodeTitle: "E01 • Glorious Purpose",
            episodeOverView: "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority."
        )
    }
}
