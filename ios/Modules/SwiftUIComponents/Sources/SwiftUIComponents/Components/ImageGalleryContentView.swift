//
//  ImageGalleryContentView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct ImageGalleryContentView: View {
    @Theme private var theme

    private let items: [ShowPosterImage]
    @Environment(\.presentationMode) private var presentationMode

    public init(items: [ShowPosterImage]) {
        self.items = items
    }

    public var body: some View {
        VStack {
            GridView(items: items, onAction: { _ in })
        }
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                HStack {
                    closeButton
                }
            }
        }
        .background(theme.colors.background)
    }

    private var closeButton: some View {
        Button {
            presentationMode.wrappedValue.dismiss()
        } label: {
            Label("Close", systemImage: "xmark.circle.fill")
                .labelStyle(.iconOnly)
        }
        .pickerStyle(.navigationLink)
        .buttonBorderShape(.roundedRectangle(radius: theme.shapes.medium))
        .buttonStyle(.bordered)
    }
}

#Preview {
    ImageGalleryContentView(
        items: [
            .init(
                tmdbId: 1234,
                title: "Arcane",
                posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg"
            ),
            .init(
                tmdbId: 123,
                title: "The Lord of the Rings: The Rings of Power",
                posterUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg"
            ),
            .init(
                tmdbId: 12346,
                title: "Kaos",
                posterUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg"
            ),
            .init(
                tmdbId: 124,
                title: "Terminator",
                posterUrl: "https://image.tmdb.org/t/p/w780/woH18JkZMYhMSWqtHkPA4F6Gd1z.jpg"
            ),
            .init(
                tmdbId: 123_346,
                title: "The Perfect Couple",
                posterUrl: "https://image.tmdb.org/t/p/w780//3buRSGVnutw8x4Lww0t70k5dG6R.jpg"
            ),
            .init(
                tmdbId: 2346,
                title: "One Piece",
                posterUrl: "https://image.tmdb.org/t/p/w780/2rmK7mnchw9Xr3XdiTFSxTTLXqv.jpg"
            ),
        ]
    )
}

public struct SwiftSeasonImage: Identifiable {
    public let id: UUID = .init()
    public let imageId: Int64
    public let imageUrl: String?

    public init(imageId: Int64, imageUrl: String?) {
        self.imageId = imageId
        self.imageUrl = imageUrl
    }
}

private enum DimensionConstants {
    static let posterColumns = [GridItem(.adaptive(minimum: 100), spacing: 8)]
    static let posterWidth: CGFloat = 130
    static let posterHeight: CGFloat = 200
    static let spacing: CGFloat = 4
}
