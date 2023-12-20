//
//  CastListView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct CastListView: View {
    let casts: [Casts]
    
    var body: some View {
        if !casts.isEmpty {
            VStack(alignment: .leading) {
                TitleView(title: "Cast")
                
                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack {
                        ForEach(casts, id: \.id) { cast in
                            CastCardView(cast: cast)
                                .padding([.leading, .trailing], 4)
                                .buttonStyle(.plain)
                                .padding(.leading, cast.id == casts.first?.id ? 16 : 0)
                                .buttonStyle(.plain)
                        }
                    }
                    .padding(.bottom)
                    .padding(.trailing)
                }
            }
        }
    }
}
