//
//  SnapCarousel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 16.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI

public struct SnapCarousel<Content: View>: View {
    @Theme private var theme

    var content: (SwiftShow) -> Content
    var list: [SwiftShow]

    private let spacing: CGFloat?
    private let trailingSpace: CGFloat
    private let additionalGesture: AnyGesture<DragGesture.Value>?
    @Binding private var index: Int

    public init(
        spacing: CGFloat? = nil,
        trailingSpace: CGFloat = 100,
        index: Binding<Int>,
        items: [SwiftShow],
        additionalGesture: AnyGesture<DragGesture.Value>? = nil,
        @ViewBuilder content: @escaping (SwiftShow) -> Content
    ) {
        list = items
        self.spacing = spacing
        self.trailingSpace = trailingSpace
        _index = index
        self.content = content
        self.additionalGesture = additionalGesture
    }

    @GestureState var offset: CGFloat = 0
    @State var currentIndex: Int = 2

    private var resolvedSpacing: CGFloat {
        spacing ?? theme.spacing.medium
    }

    public var body: some View {
        GeometryReader { proxy in
            let width = proxy.size.width - (trailingSpace - resolvedSpacing)
            let adjustmentWidth = (trailingSpace / 2) - resolvedSpacing

            HStack(spacing: resolvedSpacing) {
                ForEach(list, id: \.traktId) { item in
                    content(item)
                        .frame(width: proxy.size.width - trailingSpace)
                        .padding(.leading, currentIndex == 0 ? theme.spacing.xxxLarge : 0)
                }
            }
            .padding(.horizontal, resolvedSpacing)
            .offset(x: (CGFloat(currentIndex) * -width) + (currentIndex != 0 ? adjustmentWidth : 0) + offset)
            .gesture(
                DragGesture()
                    .updating($offset, body: { value, out, _ in
                        out = value.translation.width
                    })
                    .onEnded { value in
                        let offsetX = value.translation.width
                        let progress = -offsetX / width
                        let roundIndex = progress.rounded()
                        currentIndex = max(min(currentIndex + Int(roundIndex), list.count - 1), 0)
                        index = currentIndex
                    }
                    .onChanged { value in
                        let offsetX = value.translation.width
                        let progress = -offsetX / width
                        let roundIndex = progress.rounded()
                        index = max(min(currentIndex + Int(roundIndex), list.count - 1), 0)
                    }
            )
            .simultaneousGesture(additionalGesture ?? AnyGesture(DragGesture().onEnded { _ in }))
        }
        .animation(.easeInOut, value: offset == 0)
    }
}

#Preview {
    VStack {
        SnapCarousel(
            spacing: 10,
            trailingSpace: 120,
            index: .constant(2),
            items: [
                .init(
                    traktId: 1234,
                    title: "Arcane",
                    posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 12346,
                    title: "Kaos",
                    posterUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            additionalGesture: nil,
            content: { _ in }
        )
    }
}
