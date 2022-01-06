//
//  TextViews.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI


struct LabelTitleText : View {
	var text: String
	
	var body : some View {
		Text(text)
			.bold()
			.kerning(-0.2)
			.foregroundColor(Color("TextColor"))
			.font(.title3)
	}
}

struct LabelText : View {
	var text: String
	
	var body : some View {
		Text(text)
			.kerning(1.5)
			.font(.caption)
			.bold()
			.foregroundColor(Color("AccentColor"))
			.padding(10)
	}
}


struct TextViews_Previews: PreviewProvider {
	static var previews: some View {
		VStack(spacing: 10) {
			LabelTitleText(text: "Treniding")
			LabelText(text: "More")
		}
	}
}
