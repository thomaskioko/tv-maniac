//
//  ProviderListView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct ProviderListView: View {
    @Theme private var theme

    private let items: [SwiftProviders]

    public init(items: [SwiftProviders]) {
        self.items = items
    }

    public var body: some View {
        if !items.isEmpty {
            ChevronTitle(
                title: "Watch Providers",
                subtitle: "Provided by JustWatch"
            )

            ScrollView(.horizontal, showsIndicators: false) {
                HStack {
                    ForEach(items, id: \.providerId) { item in
                        ProviderItemView(logoUrl: item.logoUrl)
                    }
                }
                .padding([.trailing, .leading], theme.spacing.medium)
                .padding(.bottom, theme.spacing.xxSmall)
            }
        }
    }
}

#Preview {
    ProviderListView(
        items: [
            .init(
                providerId: 123,
                logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/4KAy34EHvRM25Ih8wb82AuGU7zJ.png"
            ),
            .init(
                providerId: 1233,
                logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/alqLicR1ZMHMaZGP3xRQxn9sq7p.png"
            ),
            .init(
                providerId: 23,
                logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/wwemzKWzjKYJFfCeiB57q3r4Bcm.png"
            ),
        ]
    )
}
