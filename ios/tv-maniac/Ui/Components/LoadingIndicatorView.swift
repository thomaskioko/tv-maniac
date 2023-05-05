//
//  LoadingIndicatorView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 04.12.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI

struct LoadingIndicatorView: View {
	
	let style = StrokeStyle(lineWidth: 3, lineCap: .round)
	@State var animate = false
	
    var body: some View {
		ZStack {
			Circle()
				.trim(from: 0, to: 0.2)
				.stroke(
					AngularGradient(
						gradient: .init(colors: [Color.accent]),
						center: .center
					),
					style: style
				)
				.rotationEffect(Angle(degrees: animate ? 360 : 0))
				.animation(Animation.linear(duration: 0.7).repeatForever(autoreverses: false))
				.frame(width: 100, height: 50)
			
			Circle()
				.trim(from: 0.5, to: 0.7)
				.stroke(
					AngularGradient(
						gradient: .init(colors: [Color.accent]),
						center: .center
					),
					style: style
				)
				.rotationEffect(Angle(degrees: animate ? 360 : 0))
				.animation(Animation.linear(duration: 0.7).repeatForever(autoreverses: false))
				.frame(width: 100, height: 50)
	
		}
		.padding(16)
		.edgesIgnoringSafeArea(.all)
		.onAppear(){
			self.animate.toggle()
		}
    }
}

struct LoadingIndicatorView_Previews: PreviewProvider {
    static var previews: some View {
        LoadingIndicatorView()
    }
}
