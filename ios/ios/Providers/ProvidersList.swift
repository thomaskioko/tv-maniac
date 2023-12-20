//
//  WatchProvidersList.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SDWebImageSwiftUI
import TvManiac

struct ProvidersList: View {
    var items: [Providers]
    
    var body: some View {
        if !items.isEmpty {
            TitleView(
                title: "Watch Providers",
                subtitle: "Provided by JustWatch",
                showChevron: false
            )
            
            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack {
                    ForEach(items, id: \.self) { item in
                        providerItemView(item)
                    }
                }
                .padding(.top, 8)
                .padding(.trailing, 16)
                .padding(.leading, 16)
            }
        }
    }
    
    private func providerItemView(_ item: Providers) -> some View {
        VStack(alignment: .leading) {
            if let providerUrl = item.logoUrl {
                WebImage(url: URL(string: providerUrl))
                    .resizable()
                    .placeholder {
                        VStack {
                            ProgressView()
                                .frame(
                                    width: DrawingConstants.imageWidth,
                                    height: DrawingConstants.imageHeight
                                )
                        }
                    }
                    .aspectRatio(contentMode: .fill)
                    .frame(width: DrawingConstants.imageWidth,
                           height: DrawingConstants.imageHeight)
                    .clipShape(RoundedRectangle(cornerRadius: DrawingConstants.imageRadius, style: .continuous))
                    .shadow(radius: 2)
            }
        }
    }
}

private struct DrawingConstants {
    static let imageRadius: CGFloat = 8
    static let imageWidth: CGFloat = 80
    static let imageHeight: CGFloat = 60
    static let lineLimits: Int = 1
}
