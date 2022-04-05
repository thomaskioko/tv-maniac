//
//  AirEpisodeCardView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 02.02.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac
import Kingfisher

struct AirEpisodeCardView: View {

    var episode: LastAirEpisode

    var body: some View {
        VStack(alignment: .leading) {

            HStack {

                ZStack(alignment: .top) {
                    Image(systemName: "bookmark.fill")
                            .resizable()
                            .font(Font.title.weight(.ultraLight))
                            .aspectRatio(contentMode: .fit)
                            .foregroundColor(Color.grey_500)
                            .frame(width: 48, height: 65)

                    Image(systemName: "plus")
                            .resizable()
                            .font(Font.title.weight(.light))
                            .foregroundColor(Color.grey_900)
                            .frame(width: 22, height: 22)
                            .padding(.top, 18)
                }


                VStack(alignment: .leading) {

                    Text(episode.title)
                            .padding(8)
                            .titleBoldFont(size: 23)
                            .foregroundColor(.white)
                            .background(Color.accent_color)


                    Text(episode.airDate)
                            .bodyFont(size: 18)
                            .foregroundColor(Color.text_color_bg)

                }

                Spacer()

            }

            Text(episode.name ?? "TBA")
                    .titleStyle()
                    .lineLimit(1)
                    .foregroundColor(Color.text_color_bg)

            Text(episode.overview)
                    .bodyFont(size: 16)
                    .foregroundColor(Color.text_color_bg)
                    .lineLimit(3)
                    .padding(.top, 1)

            Spacer()
        }
                .padding(16)
                .frame(width: 330, height: 210)
                .background(
                        RoundedRectangle(cornerRadius: 5)
                                .fill(Color.gradient_background)
                                .shadow(color: .grey_900, radius: 5, x: 5, y: 5)

                )
    }
}

struct AirEpisodeCardView_Previews: PreviewProvider {
    static var previews: some View {
        AirEpisodeCardView(episode: episode)

        AirEpisodeCardView(episode: episode)
                .preferredColorScheme(.dark)
    }
}
