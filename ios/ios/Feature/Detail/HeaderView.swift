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
	
	
	var viewState: ShowDetailUiViewState
	
	var body: some View {
		let resizingProcessor = ResizingImageProcessor(
			referenceSize: CGSize(width: PosterStyle.Size.max.width(), height: PosterStyle.Size.tv.height())
		)
		
		GeometryReader { geometry in
			
			ZStack(alignment: .bottom) {
				
				ShowPosterImage(
					processor: resizingProcessor,
					posterSize: .tv,
					imageUrl: viewState.tvShow.backdropImageUrl
				)
					.ignoresSafeArea()
					.frame(
						width: PosterStyle.Size.max.width(),
						height: geometry.frame(in: .global).minY + PosterStyle.Size.tv.height()
					)
					.overlay(
						ZStack(alignment: .bottom) {
							ShowPosterImage(
								processor: resizingProcessor,
								posterSize: .tv,
								imageUrl: viewState.tvShow.backdropImageUrl
							)
								.blur(radius: 50) /// blur the image
								.padding(-20) /// expand the blur a bit to cover the edges
								.clipped() /// prevent blur overflow
								.mask(Color.linearGradient) /// mask the blurred image using the gradient's alpha values
							Color.linearGradient
						}
					)
				
				
				VStack(alignment: .leading) {
					
					Text(viewState.tvShow.title)
						.titleFont(size: 30)
						.foregroundColor(Color.text_color_bg)
						.lineLimit(1)
						.frame(maxWidth: .infinity, alignment: .center)
					
					
					Text(viewState.tvShow.overview)
						.bodyFont(size: 18)
						.foregroundColor(Color.text_color_bg)
						.lineLimit(3)
						.padding(.top, 1)
					
					ShowInfoRow(show: viewState.tvShow)
						.padding(.top, 1)
					
					GenresRowView(genres: viewState.genreList)
					
					
					HStack(alignment: .center, spacing: 8) {
						
						BorderedButton(
							text: "Watch Trailer",
							systemImageName: "film.fill",
							color: .accent,
							borderColor: .grey_200,
							isOn: false,
							action: {
								
							})
						
						BorderedButton(
							text: "Follow Show",
							systemImageName: "plus.app.fill",
							color: .accent,
							borderColor: .grey_200,
							isOn: false,
							action: {
								
							})
					}
					.frame(maxWidth: .infinity, alignment: .center)
					.padding(.bottom, 16)
					.padding(.top, 4)
				}
				.padding(.trailing, 16)
				.padding(.leading, 16)
				.offset(y: -geometry.frame(in: .global).minY)
				.frame(alignment: .bottom)
			}
			Spacer()
		}
	}
	//		.statusBarStyle(.lightContent)
	
}


struct HeaderView_Previews: PreviewProvider {
	static var previews: some View {
		HeaderView(viewState: viewState)
	}
}
