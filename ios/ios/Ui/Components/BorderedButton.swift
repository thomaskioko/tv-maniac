//
//  BorderedButton.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

public struct BorderedButton : View {
	public let text: String
	public let systemImageName: String
	public let color: Color
	public let isOn: Bool
	public let action: () -> Void
	
	public init(text: String, systemImageName: String, color: Color, isOn: Bool, action: @escaping () -> Void) {
		self.text = text
		self.systemImageName = systemImageName
		self.color = color
		self.isOn = isOn
		self.action = action
	}
	
	public var body: some View {
		Button(action: {
			self.action()
		}, label: {
			HStack(alignment: .center, spacing: 4) {
				Image(systemName: systemImageName)
					.resizable()
								.aspectRatio(contentMode: .fit)
					.foregroundColor(isOn ? .white : color)
					.frame(width: 24, height: 24)
					.padding(.trailing, 16)
				
				Text(text)
					.captionStyle()
					.foregroundColor(.white)
			}
		})
			.buttonStyle(BorderlessButtonStyle())
			.padding(12)
			.background(RoundedRectangle(cornerRadius: 2)
							.stroke(color, lineWidth: 1)
							.background(isOn ? color : .clear)
							.cornerRadius(2))
	}
}

struct BorderedButton_Previews : PreviewProvider {
	static var previews: some View {
		VStack {
			BorderedButton(text: "Watch Trailer",
						   systemImageName: "film",
						   color: .maniac_yelllow,
						   isOn: false,
						   action: {
				
			})
			BorderedButton(text: "Add to wishlist",
						   systemImageName: "film",
						   color: .maniac_yelllow,
						   isOn: true,
						   action: {
				
			})
		}
	}
}
