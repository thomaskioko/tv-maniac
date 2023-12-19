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

// To for acepting List....
struct SnapCarousel<Content: View, T: DiscoverShow>: View {
    
    var content: (T) -> Content
    var list: [T]
    
    // Properties....
    var spacing: CGFloat
    var trailingSpace: CGFloat
    @Binding var index: Int
    
    init(spacing: CGFloat = 15, trailingSpace: CGFloat = 100, index: Binding<Int>, items: [T], @ViewBuilder content: @escaping (T)->Content){
        
        self.list = items
        self.spacing = spacing
        self.trailingSpace = trailingSpace
        self._index = index
        self.content = content
    }
    
    // Offset...
    @GestureState var offset: CGFloat = 0
    @State var currentIndex: Int = 2
    
    var body: some View {
        GeometryReader{proxy in
            
            // Settings correct Width for snap Carousel...
    
            // One Sided Snap Carousel
            let width = proxy.size.width - ( trailingSpace - spacing )
            let adjustMentWidth = (trailingSpace / 2) - spacing
            
            HStack (spacing: spacing) {
                ForEach(list, id: \.tmdbId) { item in
                    content(item)
                        .frame(width: proxy.size.width - trailingSpace)
                        .padding(.leading, currentIndex == 0 ? 64 : 0)
                }

            }
            
            // Spacing will be horizontal padding...
            .padding(.horizontal, spacing)
            // Setting only after 0th index...
            .offset(x: (CGFloat(currentIndex) * -width) + ( currentIndex != 0 ? adjustMentWidth : 0 ) + offset)
            .gesture(
                DragGesture()
                    .updating($offset, body: { value, out, _ in
                        out = value.translation.width
                    })
                    .onEnded({ value in
                        
                        // Updating Current Index....
                        let offsetX = value.translation.width
                        
                        // Were going to convert the tranlsation into progreess ( 0 - 1 )
                        // and round the value...
                        // based on the progress increasing or decreasing the currentInde....
                        
                        let progress = -offsetX / width
                        let roundIndex = progress.rounded()
                    
                        // setting max....
                        currentIndex = max(min(currentIndex + Int(roundIndex), list.count - 1), 0)
                    
                        // updating index....
                        currentIndex = index
                    })
                    .onChanged({ value in
                        // updating only index...
                        
                        // Updating Current Index....
                        let offsetX = value.translation.width
                        
                        // Were going to convert the tranlsation into progreess ( 0 - 1 )
                        // and round the value...
                        // based on the progress increasing or decreasing the currentInde....
                        
                        let progress = -offsetX / width
                        let roundIndex = progress.rounded()
                    
                        // setting max....
                        index = max(min(currentIndex + Int(roundIndex), list.count - 1), 0)
                    
                    })
            )
            
        }
        // Animatiing when offset = 0
        .animation(.easeInOut, value: offset == 0)
        
    }
}
