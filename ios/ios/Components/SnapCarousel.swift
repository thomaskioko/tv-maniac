//
//  SnapCarousel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 16.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import TvManiac

// See my Custom Snap Carousel Video...
// Link in Description...

// To for acepting List....
struct SnapCarousel<Content: View, T: DiscoverShow>: View {
    var content: (T) -> Content
    var list: [T]

    // Properties
    var spacing: CGFloat
    var trailingSpace: CGFloat
    @Binding var index: Int
    var additionalGesture: AnyGesture<DragGesture.Value>?

    init(spacing: CGFloat = 15, trailingSpace: CGFloat = 100, index: Binding<Int>, items: [T], additionalGesture: AnyGesture<DragGesture.Value>? = nil, @ViewBuilder content: @escaping (T)->Content){
        self.list = items
        self.spacing = spacing
        self.trailingSpace = trailingSpace
        self._index = index
        self.content = content
        self.additionalGesture = additionalGesture
    }

    // Offset
    @GestureState var offset: CGFloat = 0
    @State var currentIndex: Int = 2

    var body: some View {
        GeometryReader { proxy in
            let width = proxy.size.width - (trailingSpace - spacing)
            let adjustmentWidth = (trailingSpace / 2) - spacing

            HStack(spacing: spacing) {
                ForEach(list, id: \.tmdbId) { item in
                    content(item)
                        .frame(width: proxy.size.width - trailingSpace)
                        .padding(.leading, currentIndex == 0 ? 64 : 0)
                }
            }
            .padding(.horizontal, spacing)
            .offset(x: (CGFloat(currentIndex) * -width) + (currentIndex != 0 ? adjustmentWidth : 0) + offset)
            .gesture(
                DragGesture()
                    .updating($offset, body: { value, out, _ in
                        out = value.translation.width
                    })
                    .onEnded({ value in
                        let offsetX = value.translation.width
                        let progress = -offsetX / width
                        let roundIndex = progress.rounded()
                        currentIndex = max(min(currentIndex + Int(roundIndex), list.count - 1), 0)
                        index = currentIndex
                    })
                    .onChanged({ value in
                        let offsetX = value.translation.width
                        let progress = -offsetX / width
                        let roundIndex = progress.rounded()
                        index = max(min(currentIndex + Int(roundIndex), list.count - 1), 0)
                    })
            )
            .simultaneousGesture(additionalGesture ?? AnyGesture(DragGesture().onEnded { _ in }))
        }
        .animation(.easeInOut, value: offset == 0)
    }
}
