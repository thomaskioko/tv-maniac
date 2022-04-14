//
//  ColorExtension.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//


import Foundation
import SwiftUI

extension Font {
	

	public static func WorkSansBlack(size: CGFloat) -> Font {
		Font.custom("WorkSans-Black", size: size)
	}
	
	public static func WorkSansBold(size: CGFloat) -> Font {
		Font.custom("WorkSans-Bold", size: size).weight(.regular)
	}
	
	public static func WorkSansExtraBold(size: CGFloat) -> Font {
		Font.custom("WorkSans-ExtraBold", size: size)
	}
	
	public static func WorkSansLight(size: CGFloat) -> Font {
		Font.custom("WorkSans-Light", size: size)
	}
	
	public static func WorkSansExtraLight(size: CGFloat) -> Font {
		Font.custom("WorkSans-ExtraLight", size: size)
	}
	
	public static func WorkSansMedium(size: CGFloat) -> Font {
		Font.custom("WorkSans-Medium", size: size)
	}
	
	public static func WorkSansRegular(size: CGFloat) -> Font {
		Font.custom("WorkSans-Regular", size: size)
	}

	public static func WorkSansSemiBold(size: CGFloat) -> Font {
		Font.custom("WorkSans-Semibold", size: size)
	}
	
	public static func WorkSansThin(size: CGFloat) -> Font {
		Font.custom("WorkSans-Thin", size: size)
	}

}

struct TitleFont: ViewModifier {
	let size: CGFloat
	
	func body(content: Content) -> some View {
		content.font(.WorkSansBlack(size: size))
	}
}


struct TitleBlackFont: ViewModifier {
	let size: CGFloat
	
	func body(content: Content) -> some View {
		content.font(.WorkSansSemiBold(size: size))
	}
}

struct TitleSemiBoldFont: ViewModifier {
	let size: CGFloat
	
	func body(content: Content) -> some View {
		content.font(.WorkSansBlack(size: size))
	}
}



struct TitleBoldFont: ViewModifier {
	let size: CGFloat
	
	func body(content: Content) -> some View {
		content.font(.WorkSansBold(size: size))
	}
}

struct BodyFont: ViewModifier {
	let size: CGFloat
	
	func body(content: Content) -> some View {
		content.font(.WorkSansRegular(size: size))
	}
}

struct BodyMediumFont: ViewModifier {
	let size: CGFloat
	
	func body(content: Content) -> some View {
		return content.font(.WorkSansMedium(size: size))
	}
}

struct CaptionFont: ViewModifier {
	let size: CGFloat
	
	func body(content: Content) -> some View {
		content.font(.WorkSansLight(size: size))
	}
}



extension View {
	func titleFont(size: CGFloat) -> some View {
		ModifiedContent(content: self, modifier: TitleFont(size: size))
	}
	
	func titleBoldFont(size: CGFloat) -> some View {
		ModifiedContent(content: self, modifier: TitleBoldFont(size: size))
	}
	
	func titleSemiBoldFont(size: CGFloat) -> some View {
		ModifiedContent(content: self, modifier: TitleSemiBoldFont(size: size))
	}
	
	func titleStyle() -> some View {
		ModifiedContent(content: self, modifier: TitleFont(size: 16))
	}
	
	func bodyFont(size: CGFloat) -> some View {
		ModifiedContent(content: self, modifier: BodyFont(size: size))
	}
	
	func bodyMediumFont(size: CGFloat) -> some View {
		ModifiedContent(content: self, modifier: BodyMediumFont(size: size))
	}
	
	func captionStyle() -> some View {
		ModifiedContent(content: self, modifier: CaptionFont(size: 16))
	}
	
	func captionFont(size: CGFloat) -> some View {
		ModifiedContent(content: self, modifier: CaptionFont(size: size))
	}
	
}

