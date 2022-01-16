//
//  SnapCarousel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 16.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

// See my Custom Snap Carousel Video...
// Link in Description...

struct SnapCarousel<Content: View, T: ShowUiModel>: View {
    var content: (T) -> Content
    var list: [T]

    // Properties...
    var spacing: CGFloat
    var trailingSpace: CGFloat
    @Binding var index: Int

    init(
            spacing: CGFloat = 15,
            trailingSpace: CGFloat = 100,
            index: Binding<Int>,
            items: [T],
            @ViewBuilder content: @escaping (T) -> Content
    ) {

        self.list = items
        self.spacing = spacing
        self.trailingSpace = trailingSpace
        self._index = index
        self.content = content
    }

    // Offset...
    @GestureState var offset: CGFloat = 0
    @SwiftUI.State var currentIndex: Int = 0

    var body: some View {

        GeometryReader { proxy in

            let width = proxy.size.width - (trailingSpace - spacing)
            let adjustmentWidth = (trailingSpace / 2) - spacing

            HStack(spacing: spacing) {

                ForEach(list, id: \.self) { item in
                    content(item)
                            .frame(width: proxy.size.width - trailingSpace)
                            .offset(y: getOffset(item: item, width: width))
                }
            }
                    // Spacing will be horizontal padding...
                    .padding(.horizontal, spacing)
                    // setting only after 0th index..
                    .offset(x: (CGFloat(currentIndex) * -width) + (currentIndex != 0 ? adjustmentWidth : 0) + offset)
                    .gesture(

                            DragGesture()
                                    .updating($offset, body: { value, out, _ in

                                        out = value.translation.width
                                    })
                                    .onEnded({ value in

                                        // Updating Current Index....
                                        let offsetX = value.translation.width

                                        // were going to convert the tranlsation into progress (0 - 1)
                                        // and round the value...
                                        // based on the progress increasing or decreasing the currentIndex...

                                        let progress = -offsetX / width

                                        let roundIndex = progress.rounded()

                                        // setting min...
                                        currentIndex = max(min(currentIndex + Int(roundIndex), list.count - 1), 0)

                                        // updating index....
                                        currentIndex = index
                                    })
                                    .onChanged({ value in
                                        // updating only index....

                                        // Updating Current Index....
                                        let offsetX = value.translation.width

                                        // were going to convert the tranlsation into progress (0 - 1)
                                        // and round the value...
                                        // based on the progress increasing or decreasing the currentIndex...

                                        let progress = -offsetX / width

                                        let roundIndex = progress.rounded()

                                        // setting min...
                                        index = max(min(currentIndex + Int(roundIndex), list.count - 1), 0)
                                    })
                    )
        }
                // Animating when offset = 0
                .animation(.easeInOut, value: offset == 0)
    }

    // Moving View based on scroll Offset...
    func getOffset(item: T, width: CGFloat) -> CGFloat {

        // Progress...
        // Shifting Current Item to Top....
        let progress = ((offset < 0 ? offset : -offset) / width) * 60

        // max 60...
        // then again minus from 60....
        let topOffset = -progress < 60 ? progress : -(progress + 120)

        let previous = getIndex(item: item) - 1 == currentIndex ? (offset < 0 ? topOffset : -topOffset) : 0

        let next = getIndex(item: item) + 1 == currentIndex ? (offset < 0 ? -topOffset : topOffset) : 0

        // Saftey check between 0 to max list size...
        let checkBetween = currentIndex >= 0 && currentIndex < list.count ? (getIndex(item: item) - 1 == currentIndex ? previous : next) : 0

        // checking current....
        // if so shifting view to top...
        return getIndex(item: item) == currentIndex ? -60 - topOffset : checkBetween
    }

    // Fetching Index...
    func getIndex(item: T) -> Int {
        let index = list.firstIndex { currentItem in
            return currentItem.id == item.id
        } ?? 0

        return index
    }
}
