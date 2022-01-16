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
	
	var show: ShowUiModel
	
	var body: some View {
		HStack {
			
			if show.status.isEmpty != true {
				Text(show.status)
					.captionFont(size: 16)
					.foregroundColor(.yellow_300)
					.padding(2)
					.background(Color.yellow_300.opacity(0.2))
				
				Text("•")
					.bodyFont(size: 16)
					.foregroundColor(.yellow_300)
			}
			
			if let year = show.year {
				Text(year)
					.captionFont(size: 16)
					.foregroundColor(.white)
			}
			
			Text("•")
				.bodyFont(size: 16)
				.foregroundColor(.yellow_300)
			
			if let language = show.language {
				Text(language)
					.captionFont(size: 16)
					.textCase(.uppercase)
					.foregroundColor(.white)
			}
			
			Text("•")
				.bodyFont(size: 16)
				.foregroundColor(.yellow_300)
			
			if let averageVotes = show.averageVotes {
				Text(String(format: "%.1f", averageVotes))
					.captionFont(size: 16)
			}
			
			Text("•")
				.bodyFont(size: 16)
				.foregroundColor(.yellow_300)
			
		
		}
		.foregroundColor(.white)
	}
}

struct ShowInfoRow_Previews: PreviewProvider {
	static var previews: some View {
		ShowInfoRow(show: mockShow).background(Color.black)
	}
}
