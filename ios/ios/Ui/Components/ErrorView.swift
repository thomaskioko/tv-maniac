//
//  ErrorUi.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.11.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct ErrorView: View {
	
	var errorMessage: String
	
	var body: some View {
		VStack {
			
			Image(systemName: "exclamationmark.triangle")
				.resizable()
				.aspectRatio(contentMode: .fit)
				.foregroundColor(Color.accent)
				.font(Font.title.weight(.light))
				.frame(width: 120, height: 120)
			
			Text(errorMessage)
				.bodyMediumFont(size: 16)
				.foregroundColor(.accent_color)
				.padding(.top, 18)
				.padding(.trailing, 16)
				.padding(.leading, 16)
			
		}.padding(16)
			.background(Color("Background"))
		 .edgesIgnoringSafeArea(.all)
		
	}
}
