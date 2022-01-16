//
//  ShowDetailView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct ShowDetailView: View {
	var show: ShowUiModel
	
	var body: some View {
		ZStack {
			ScrollView(.vertical, showsIndicators: false) {
				HeaderView(show: show)
					.frame(width: PosterStyle.Size.max.width(), height: PosterStyle.Size.max.height())

				VStack(alignment: .leading, spacing: 15){
					ShowBodyView(show: show)
				}
			}
			.background(Color.grey_900)
		}
		.edgesIgnoringSafeArea(.all)
	}
}

struct ShowDetailView_Previews: PreviewProvider {
	static var previews: some View {
		ShowDetailView(show: mockShow)
	}
}
