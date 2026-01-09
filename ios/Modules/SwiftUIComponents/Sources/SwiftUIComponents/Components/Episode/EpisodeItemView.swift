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
    private let isWatched: Bool
    private let isEpisodeUpdating: Bool
    private let daysUntilAir: Int64?
    private let hasAired: Bool
    private let dayLabelFormat: (_ count: Int) -> String
    private let episodeWidth: CGFloat
    private let episodeHeight: CGFloat
    private let shadowRadius: CGFloat
    private let cornerRadius: CGFloat
    private let posterRadius: CGFloat
    private let onWatchedToggle: () -> Void

    public init(
        imageUrl: String?,
        episodeTitle: String,
        episodeOverView: String,
        isWatched: Bool = false,
        isEpisodeUpdating: Bool = false,
        daysUntilAir: Int64? = nil,
        hasAired: Bool = true,
        dayLabelFormat: @escaping (_ count: Int) -> String = { count in count == 1 ? "day" : "days" },
        episodeWidth: CGFloat = Constants.defaultEpisodeWidth,
        episodeHeight: CGFloat = Constants.defaultEpisodeHeight,
        shadowRadius: CGFloat = Constants.defaultShadowRadius,
        cornerRadius: CGFloat = Constants.defaultCornerRadius,
        posterRadius: CGFloat = Constants.defaultPosterRadius,
        onWatchedToggle: @escaping () -> Void = {}
    ) {
        self.imageUrl = imageUrl
        self.episodeTitle = episodeTitle
        self.episodeOverView = episodeOverView
        self.isWatched = isWatched
        self.isEpisodeUpdating = isEpisodeUpdating
        self.daysUntilAir = daysUntilAir
        self.hasAired = hasAired
        self.dayLabelFormat = dayLabelFormat
        self.episodeWidth = episodeWidth
        self.episodeHeight = episodeHeight
        self.shadowRadius = shadowRadius
        self.cornerRadius = cornerRadius
        self.posterRadius = posterRadius
        self.onWatchedToggle = onWatchedToggle
    }

    public var body: some View {
        HStack(spacing: 0) {
            posterView
            episodeDetails
            watchedButton
        }
        .frame(height: episodeHeight)
        .frame(maxWidth: .infinity)
        .background(theme.colors.surface)
        .cornerRadius(cornerRadius)
        .padding(.horizontal, theme.spacing.medium)
    }

    private var posterView: some View {
        PosterItemView(
            title: nil,
            posterUrl: imageUrl,
            posterWidth: episodeWidth,
            posterHeight: episodeHeight,
            posterRadius: posterRadius
        )
        .frame(width: episodeWidth, height: episodeHeight)
        .clipped()
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

    @ViewBuilder
    private var watchedButton: some View {
        if isEpisodeUpdating {
            ProgressView()
                .frame(width: DimensionConstants.checkmarkSize, height: DimensionConstants.checkmarkSize)
                .padding(.trailing, theme.spacing.medium)
        } else if hasAired {
            Button(action: onWatchedToggle) {
                ZStack {
                    Circle()
                        .fill(isWatched ? theme.colors.success : theme.colors.grey)
                        .frame(width: DimensionConstants.checkmarkSize, height: DimensionConstants.checkmarkSize)
                    Image(systemName: "checkmark")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.white)
                }
            }
            .buttonStyle(.plain)
            .padding(.trailing, theme.spacing.medium)
        } else if let daysUntilAir, daysUntilAir > 0 {
            VStack(spacing: 0) {
                Text("\(daysUntilAir)")
                    .textStyle(theme.typography.titleLarge)
                    .foregroundColor(theme.colors.onSurfaceVariant)
                Text(dayLabelFormat(Int(daysUntilAir)))
                    .textStyle(theme.typography.labelSmall)
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }
            .padding(.trailing, theme.spacing.medium)
        } else {
            Text("TBD")
                .textStyle(theme.typography.titleMedium)
                .foregroundColor(theme.colors.onSurfaceVariant)
                .padding(.trailing, theme.spacing.medium)
        }
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

private enum DimensionConstants {
    static let checkmarkSize: CGFloat = 24
}

#Preview {
    VStack {
        EpisodeItemView(
            imageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            episodeTitle: "E01 • Glorious Purpose",
            episodeOverView: "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
            isWatched: true
        )

        EpisodeItemView(
            imageUrl: nil,
            episodeTitle: "E02 • The Variant",
            episodeOverView: "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
            isWatched: false
        )

        EpisodeItemView(
            imageUrl: nil,
            episodeTitle: "E03 • Future Episode",
            episodeOverView: "This episode will air in 7 days.",
            isWatched: false,
            daysUntilAir: 7,
            hasAired: false
        )

        EpisodeItemView(
            imageUrl: nil,
            episodeTitle: "E04 • Unknown Air Date",
            episodeOverView: "This episode has no known air date yet.",
            isWatched: false,
            hasAired: false
        )
    }
    .themedPreview()
}
