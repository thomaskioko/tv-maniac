//
//  ColorExtension.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

import SwiftUI


extension Color {
	
	public static var linearGradient = LinearGradient(
		gradient: Gradient(stops: [
			.init(color: .grey_900, location: 0),
			.init(color: .clear, location: 0.9)
		]),
		startPoint: .bottom,
		endPoint: .top
	)
	
	public static var accent_color: Color {
		Color("AccentColor", bundle: nil)
	}
	
	public static var yellow_300: Color {
		Color("yellow_300", bundle: nil)
	}
	
	public static var yellow_500: Color {
		Color("yellow_500", bundle: nil)
	}
	
	public static var grey_900: Color {
		Color("grey_900", bundle: nil)
	}
	
}
