//
//  HeaderView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 14.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct HeaderView: View {

	
	//TODO User state from stateMachine and replace viewState reference
    var viewState: ShowDetailUiViewState
    var topEdge: CGFloat
    var maxHeight: CGFloat

    @Binding var offset: CGFloat
    let onFollowShowClicked: (Int32) -> Void

    var body: some View {

        ZStack(alignment: .bottom) {
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

            VStack {

                Text(viewState.tvShow.title)
                        .titleFont(size: 30)
                        .foregroundColor(Color.text_color_bg)
                        .lineLimit(1)
                        .padding(.top, 8)

                Text(viewState.tvShow.overview)
                        .bodyFont(size: 18)
                        .foregroundColor(Color.text_color_bg)
                        .lineLimit(3)
                        .padding(.top, 1)

                ShowInfoRow(show: viewState.tvShow)
                        .padding(.top, 1)

				GenresRowView(genres: viewState.tvShow.genres)

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
                                onFollowShowClicked(viewState.tvShow.traktId)
                            }
                    )
                }
                        .padding(.bottom, 16)
                        .padding(.top, 10)
            }
                    .padding(.horizontal)

        }
                .frame(width: getRect().width, height: maxHeight, alignment: .center)
                .opacity(getOpacity())

    }

    // Calculation Opacity...
    func getOpacity() -> CGFloat {

        let progress = -(offset + 80) / (maxHeight - (120 + topEdge))

        let opacity = 1 - progress


        return offset < 0 ? opacity : 1
    }


}


struct HeaderView_Previews: PreviewProvider {
    static private var offset = Binding.constant(CGFloat(0))
    static var previews: some View {
        HeaderView(
                viewState: viewState,
                topEdge: 10,
                maxHeight: 720,
                offset: offset
        ) { (int64: Int32) -> () in
        }
    }
}
