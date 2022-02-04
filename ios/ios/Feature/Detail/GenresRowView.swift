//
//  GenresRow.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct GenresRowView: View {
	
	let genres: [GenreUIModel]
	var body: some View {
		
		ScrollView(.horizontal, showsIndicators: false) {
			HStack(alignment: .center, spacing: 4) {
				ForEach(genres, id: \.self) { genre in
					Text(genre.name)
						.captionFont(size: 16)
						.foregroundColor(Color.text_color_bg)
						.padding(10)
						.background(Color.accent.opacity(0.12))
						.cornerRadius(5)
				}
			}
		}
	}
}

struct GenresRow_Previews: PreviewProvider {
	static var previews: some View {
		GenresRowView(genres: genreList)
	}
}
