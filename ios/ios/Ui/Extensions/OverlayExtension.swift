//
//  ColorExtension.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI


// tint overlay over the background photo
struct TintOverlay: View {
	var body: some View {
		ZStack {
			Text(" ")
				.foregroundColor(.white)
		}
		.background(
			Color.linearGradient
				.edgesIgnoringSafeArea(.all)
		)
		.frame(width: UIScreen.main.bounds.width, height: 600)
	}
}
