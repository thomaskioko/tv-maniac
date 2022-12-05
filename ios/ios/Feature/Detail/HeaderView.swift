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

    var show: Show
    var topEdge: CGFloat
    var maxHeight: CGFloat

    @Binding var offset: CGFloat
    let onFollowShowClicked: (Int32) -> Void

    var body: some View {

        ZStack(alignment: .bottom) {
            ShowPosterImage(
                    posterSize: .max,
                    imageUrl: show.backdropImageUrl
            )
                    .aspectRatio(contentMode: .fill)
                    .overlay(
                            ZStack(alignment: .bottom) {
                                ShowPosterImage(
                                        posterSize: .max,
                                        imageUrl: show.backdropImageUrl
                                )
                                        .blur(radius: 50) /// blur the image
                                        .clipped() /// prevent blur overflow
                                        .mask(Color.linearGradient) /// mask the blurred image using the gradient's alpha values
                                Color.linearGradient
                            }
                    )

            VStack {

                Text(show.title)
                        .titleFont(size: 30)
                        .foregroundColor(Color.text_color_bg)
                        .lineLimit(1)
                        .padding(.top, 8)

                Text(show.overview)
                        .bodyFont(size: 18)
                        .foregroundColor(Color.text_color_bg)
                        .lineLimit(3)
                        .padding(.top, 1)

                ShowInfoRow(show: show)
                        .padding(.top, 1)

                GenresRowView(genres: show.genres)

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
                                onFollowShowClicked(show.traktId)
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
                show: mockShow,
                topEdge: 10,
                maxHeight: 720,
                offset: offset
        ) { (int64: Int32) -> () in
        }
    }
}
