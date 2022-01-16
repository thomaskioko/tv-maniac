//
//  ShowBodyView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 16.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct ShowBodyView: View {
	
	var show: ShowUiModel
	
	var body: some View {
		
		VStack(alignment: .leading) {
			

		}
		.padding(.horizontal)
		.background(Color.grey_900)
	}
}

struct ShowBodyView_Previews: PreviewProvider {
	static var previews: some View {
		ShowBodyView(show: mockShow)
	}
}
