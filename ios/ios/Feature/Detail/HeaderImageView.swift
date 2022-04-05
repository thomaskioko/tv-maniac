//
//  HeaderView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 14.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct HeaderImageView: View {

    var viewState: ShowDetailUiViewState

    var body: some View {

        GeometryReader { geometry in

            ShowPosterImage(
                    posterSize: .max,
                    imageUrl: viewState.tvShow.backdropImageUrl
            )
                    .aspectRatio(contentMode: .fill)
                    .overlay(
                            ZStack(alignment: .bottom) {
                                ShowPosterImage(
                                        posterSize: .max,
                                        imageUrl: viewState.tvShow.backdropImageUrl
                                )
                                        .blur(radius: 50) /// blur the image
                                        .clipped() /// prevent blur overflow
                                        .mask(Color.linearGradient) /// mask the blurred image using the gradient's alpha values
                                Color.linearGradient
                            }
                    )
                    .offset(y: -geometry.frame(in: .global).minY)
                    .frame(width: UIScreen.main.bounds.width, height: geometry.frame(in: .global).minY + 520)

        }
                .frame(height: 520)
    }
}


struct HeaderView_Previews: PreviewProvider {
    static var previews: some View {
        HeaderImageView(viewState: viewState)
    }
}
