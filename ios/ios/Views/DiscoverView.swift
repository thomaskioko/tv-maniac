//
//  DiscoverView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI

struct DiscoverView: View {

    var body: some View {
		ZStack {
			VStack {
				Text("Discover Shows")
					
				Spacer()
			}
			.frame(width : CGFloat(480.0))
			.background(Color("Background"))
			
		}
    }
}


struct DiscoverView_Previews: PreviewProvider {
    static var previews: some View {
        DiscoverView()
		
		DiscoverView()
			.preferredColorScheme(.dark)
    }
}
