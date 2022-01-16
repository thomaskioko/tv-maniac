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
        let resizingProcessor = ResizingImageProcessor(
                referenceSize: CGSize(width: PosterStyle.Size.tv.width(), height: PosterStyle.Size.tv.height())
        )

        GeometryReader { geometry in

            ZStack(alignment: .bottom) {

                ShowPosterImage(
                        processor: resizingProcessor,
                        posterSize: .tv,
                        imageUrl: show.backdropImageUrl
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
                                            imageUrl: show.backdropImageUrl
                                    )
                                            .blur(radius: 50) /// blur the image
                                            .padding(-20) /// expand the blur a bit to cover the edges
                                            .clipped() /// prevent blur overflow
                                            .mask(Color.linearGradient) /// mask the blurred image using the gradient's alpha values
                                    Color.linearGradient
                                }
                        )


                VStack(alignment: .leading) {

                    Text(show.title)
                            .titleFont(size: 30)
                            .foregroundColor(.white)
                            .lineLimit(1)
                            .frame(maxWidth: .infinity, alignment: .center)


                    Text(show.overview)
                            .bodyFont(size: 18)
                            .foregroundColor(.white)
                            .lineLimit(3)
                            .padding(.top, 1)

                    ShowInfoRow(show: show)
                            .padding(.top, 1)

                    GenresRowView(genres: genreList)


                    HStack(alignment: .center, spacing: 8) {

                        BorderedButton(
                                text: "Trailer",
                                systemImageName: "film",
                                color: .yellow_300,
                                isOn: false,
                                action: {

                                })

                        BorderedButton(
                                text: "Add to watchlist",
                                systemImageName: "text.badge.plus",
                                color: .yellow_300,
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
        HeaderView(show: mockShow)
    }
}
