//
//  ShowInfoRow.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct ShowInfoRow: View {
	
	var show: Show
	
	var body: some View {
		ScrollView(.horizontal, showsIndicators: false) {
			HStack(alignment: .center) {

				if show.status?.isEmpty != true {
					Text(show.status ?? "")
							.captionFont(size: 16)
							.foregroundColor(.accent)
							.padding(3)
							.background(Color.accent.opacity(0.2))
							.cornerRadius(2)

					Text("•")
							.bodyFont(size: 16)
							.foregroundColor(.accent)
				}

                Text(show.year)
                        .captionFont(size: 16)
                        .foregroundColor(Color.text_color_bg)

				Text("•")
						.bodyFont(size: 16)
						.foregroundColor(.accent)

				if let language = show.language {
					Text(language)
							.captionFont(size: 16)
							.textCase(.uppercase)
							.foregroundColor(Color.text_color_bg)
				}

				Text("•")
						.bodyFont(size: 16)
						.foregroundColor(.accent)

                Text(String(format: "%.1f", show.rating))
                        .captionFont(size: 16)
                        .foregroundColor(Color.text_color_bg)

				Text("•")
						.bodyFont(size: 16)
						.foregroundColor(.accent)
						.foregroundColor(Color.text_color_bg)


			}
					.foregroundColor(Color.white)
		}
	}
}

struct ShowInfoRow_Previews: PreviewProvider {
	static var previews: some View {
		ShowInfoRow(show: mockShow)
		
		ShowInfoRow(show: mockShow)
			.preferredColorScheme(.dark)
	}
}
