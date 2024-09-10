//
//  ItemContentPosterView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SDWebImageSwiftUI
import SwiftUI

public struct PosterItemView: View {
  private let title: String
  private let posterUrl: String?
  private let libraryImageOverlay: String
  private let isInLibrary: Bool
  private let posterWidth: CGFloat
  private let posterHeight: CGFloat

  public init(
    title: String,
    posterUrl: String?,
    libraryImageOverlay: String = "square.stack.fill",
    isInLibrary: Bool = false,
    posterWidth: CGFloat = 120,
    posterHeight: CGFloat = 180
  ) {
    self.title = title
    self.posterUrl = posterUrl
    self.libraryImageOverlay = libraryImageOverlay
    self.isInLibrary = isInLibrary
    self.posterWidth = posterWidth
    self.posterHeight = posterHeight
  }

  public var body: some View {
    if let posterUrl = posterUrl {
      WebImage(url: URL(string: posterUrl), options: .highPriority) { image in
        image.resizable()
      } placeholder: {
        PosterPlaceholder(
          title: title,
          posterWidth: posterWidth,
          posterHeight: posterHeight
        )
      }
      .aspectRatio(contentMode: .fill)
      .overlay {
        OverlayBackground(
          isInLibrary: isInLibrary,
          libraryImageOverlay: libraryImageOverlay
        )
        .frame(width: posterWidth)
      }
      .transition(.opacity)
      .frame(width: posterWidth, height: posterHeight)
      .clipShape(
        RoundedRectangle(
          cornerRadius: DimensionConstants.posterRadius,
          style: .continuous
        )
      )
    } else {
      PosterPlaceholder(
        title: title,
        posterWidth: posterWidth,
        posterHeight: posterHeight
      )
    }
  }
}

@ViewBuilder
private func OverlayBackground(
  isInLibrary: Bool,
  libraryImageOverlay: String
) -> some View {
  ZStack {
    if isInLibrary {
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
              LinearGradient(colors:
                [Color.black,
                 Color.black.opacity(0.924),
                 Color.black.opacity(0.707),
                 Color.black.opacity(0.383),
                 Color.black.opacity(0)],
                startPoint: .bottom,
                endPoint: .top)
            }
        }
      }
    }
  }
}

private enum DimensionConstants {
  static let posterRadius: CGFloat = 4
  static let shadowRadius: CGFloat = 2
}

#Preview {
  PosterItemView(
    title: "Arcane",
    posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
    isInLibrary: true,
    posterWidth: CGFloat(160),
    posterHeight: CGFloat(240)
  )
}
