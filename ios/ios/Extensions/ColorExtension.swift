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
			 .init(color: .black, location: 0),
			 .init(color: .clear, location: 0.8)
		 ]),
		 startPoint: .bottom,
		 endPoint: .top
	 )
	
	public static var accent_color: Color {
		Color("AccentColor", bundle: nil)
	}
	public static var maniac_yelllow: Color {
		Color("maniac_yelllow", bundle: nil)
	}
  
}
