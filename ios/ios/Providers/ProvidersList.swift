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
                                    width: DimensionConstants.imageWidth,
                                    height: DimensionConstants.imageHeight
                                )
                        }
                    }
                    .aspectRatio(contentMode: .fill)
                    .frame(width: DimensionConstants.imageWidth,
                           height: DimensionConstants.imageHeight)
                    .clipShape(RoundedRectangle(cornerRadius: DimensionConstants.imageRadius, style: .continuous))
                    .shadow(radius: 8)
            }
        }
    }
}

private struct DimensionConstants {
    static let imageRadius: CGFloat = 4
    static let imageWidth: CGFloat = 80
    static let imageHeight: CGFloat = 70
    static let lineLimits: Int = 1
}
