//
//  ImageGalleryContentView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac

struct ImageGalleryContentView: View {
    
    var items: [SeasonImagesModel]
    
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        VStack {
            ScrollView(.vertical, showsIndicators: false) {
                LazyVGrid(columns: DimensionConstants.posterColumns,spacing: DimensionConstants.spacing){
                    ForEach(items, id: \.id){ item in
                        PosterItemView(
                            title: "",
                            posterUrl: item.imageUrl,
                            posterWidth: DimensionConstants.posterWidth,
                            posterHeight: DimensionConstants.posterHeight
                        )
                    }
                }.padding(.all, 10)
            }
        }
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                HStack {
                    closeButton
                }
            }
        }
        .background(Color.background)
    }
    
    private var closeButton: some View {
        Button {
            presentationMode.wrappedValue.dismiss() 
        } label: {
            Label("Close", systemImage: "xmark.circle.fill")
                .labelStyle(.iconOnly)
        }
        .pickerStyle(.navigationLink)
        .buttonBorderShape(.roundedRectangle(radius: 16))
        .buttonStyle(.bordered)
    }
}

private struct DimensionConstants {
    static let posterColumns = [GridItem(.adaptive(minimum: 100), spacing: 8)]
    static let posterWidth: CGFloat = 130
    static let posterHeight: CGFloat = 200
    static let spacing: CGFloat = 4
}
