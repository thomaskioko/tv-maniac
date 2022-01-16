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
		HStack{
			
			ForEach(genres, id: \.self) { genre in
				
				Button(action: {}){
					Text(genre.name)
						.bodyFont(size: 16)
						.foregroundColor(.white)
						.padding(10)
						.background(Color.yellow_300.opacity(0.12))
				}
				.cornerRadius(2)
			
			
		}
	}
	}
}

struct GenresRow_Previews: PreviewProvider {
	static var previews: some View {
		GenresRowView(genres: genreList)
	}
}
