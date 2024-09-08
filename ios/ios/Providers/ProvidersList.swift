//
//  WatchProvidersList.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import SDWebImageSwiftUI
import TvManiac

struct ProvidersList: View {
    var items: [Providers]
    
    var body: some View {
        if !items.isEmpty {
            ChevronTitle(
                title: "Watch Providers",
                subtitle: "Provided by JustWatch"
            )

            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack {
                    ForEach(items, id: \.self) { item in
                        ProviderItemView(logoUrl: item.logoUrl)
                    }
                }
                .padding(.trailing, 16)
                .padding(.leading, 16)
                .padding(.bottom, 4)
            }
        }
    }

}
