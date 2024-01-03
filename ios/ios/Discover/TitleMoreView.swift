//
//  TitleMoreView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 02.01.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

import SwiftUI

struct TitleMoreView: View {
    let title: String
    var onMoreClicked : () -> Void
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                HStack {
                    Text(title)
                        .fontWeight(.semibold)
                        .fontDesign(.rounded)
                        .font(.title3)
                    
                    Spacer()
                    
                    Button(
                        action: { onMoreClicked() },
                        label: {
                            Text("More")
                                .fontDesign(.rounded)
                                .foregroundColor(Color.accent.opacity(0.8))
                                .font(.callout)
                                .alignmentGuide(.view) { d in d[HorizontalAlignment.center] }
                            
                            Image(systemName: "chevron.right")
                                .foregroundColor(Color.accent.opacity(0.8))
                                .font(.callout)
                        }
                    )
                }
                .padding([.leading, .trailing])
            }
        }
    }
}

#Preview {
    TitleMoreView(title: "Coming Soon", onMoreClicked: {})
}
