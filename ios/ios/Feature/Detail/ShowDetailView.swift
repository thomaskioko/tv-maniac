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
			ScrollView(showsIndicators: false) {
				HeaderView(show: show)
			}
		}.edgesIgnoringSafeArea(.all)
    }
}

struct ShowDetailView_Previews: PreviewProvider {
    static var previews: some View {
		ShowDetailView(show: mockShow)
    }
}
