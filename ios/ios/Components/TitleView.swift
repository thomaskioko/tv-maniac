//
//  TitleView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct TitleView: View {
    let title: String
    var subtitle: String?
    var showChevron = false
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                HStack {
                    Text(title)
                        .padding([.top, .leading])
                        .fontWeight(.semibold)
                        .fontDesign(.rounded)
                        .font(.title3)

                    if showChevron {
                        Image(systemName: "chevron.right")
                            .fontDesign(.rounded)
                            .font(.callout)
                            .fontWeight(.regular)
                            .foregroundColor(.secondary)
                            .padding(.top)
                            .accessibilityHidden(true)
                    }
                }
                if let subtitle {
                    HStack {
                        Text(NSLocalizedString(subtitle, comment: ""))
                            .fontDesign(.rounded)
                            .foregroundColor(.secondary)
                            .padding(.leading)
                            .font(.callout)
                    }
                }
            }
            Spacer()
        }
        .accessibilityElement(children: .combine)
    }
}

#Preview {
    TitleView(title: "Coming Soon", subtitle: "From Watchlist")
}
