//
//  SearchView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI

struct SearchView: View {
	var body: some View {
		ZStack {
			VStack {
				Text("Search Shows")
					
				Spacer()
			}
			.frame(width : CGFloat(480.0))
			.background(Color("Background"))
			
		}
	}
}

struct SearchView_Previews: PreviewProvider {
    static var previews: some View {
        SearchView()
		
		SearchView()
			.preferredColorScheme(.dark)
    }
}
