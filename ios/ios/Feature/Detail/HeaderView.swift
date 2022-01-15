//
//  HeaderView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 14.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac
import Kingfisher

struct HeaderView: View {
	
	
	var show: ShowUiModel
	
	var body: some View {
		let resizingProcessor = ResizingImageProcessor(referenceSize: CGSize(width: PosterStyle.Size.max.width(), height: 600))
		
		VStack {
			
			ZStack {
				ShowPosterImage(
					processor: resizingProcessor,
					posterSize: .max,
					posterImageUrl: show.backdropImageUrl
				)
					.aspectRatio(contentMode: .fit)
					.overlay(
					ZStack(alignment: .bottom) {
						ShowPosterImage(
							processor: resizingProcessor,
							posterSize: .max,
							posterImageUrl: show.backdropImageUrl
						)
							.blur(radius: 20) /// blur the image
							.padding(-20) /// expand the blur a bit to cover the edges
							.clipped() /// prevent blur overflow
							.mask(Color.linearGradient) /// mask the blurred image using the gradient's alpha values
						
						
						Color.linearGradient
					}
					
				)
				
				
				VStack(alignment: .leading) {
					
					Text(show.title)
						.titleFont(size: 28)
						.foregroundColor(.white)
						.lineLimit(1)
						.frame(maxWidth: .infinity, alignment: .center)
					
					
					Text(show.overview)
						.captionFont(size: 18)
						.foregroundColor(.white)
						.lineLimit(4)
						.padding(.top, 2)
					
					ShowInfoRow(show: show)
						.padding(.top, 1)
					
					GenresRowView(genres: genreList)
						
					
					HStack(alignment: .center, spacing: 8) {
						
						BorderedButton(
							text: "Watch Trailer",
							systemImageName: "film",
							color: .maniac_yelllow,
							isOn: false,
							action: {
								
							})
						
						BorderedButton(
							text: "Add to watchlist",
							systemImageName: "text.badge.plus",
							color: .maniac_yelllow,
							isOn: false,
							action: {
								
							})
					}
					.frame(maxWidth: .infinity, alignment: .center)
					.padding(.bottom, 16)
					.padding(.top, 4)
					
					
				}
				.frame(height: 600, alignment: .bottom)
				.padding(.trailing, 16)
				.padding(.leading, 16)
				
				
			}
			Spacer()
		}
		
	}
	//		.statusBarStyle(.lightContent)
	
}



struct HeaderView_Previews: PreviewProvider {
	static var previews: some View {
		HeaderView(show: mockShow)
	}
}
