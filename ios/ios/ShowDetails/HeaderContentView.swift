//
//  HeaderContentView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct HeaderContentView: View {
    
    let show: ShowDetails
    var progress: CGFloat
    var maxHeight : CGFloat
    var onAddToLibraryClick : (Bool) -> Void
    var onWatchTrailerClick : (Int64) -> Void

    var body: some View {
        
        ZStack {
            HeaderCoverArtWorkView(
                backdropImageUrl: show.backdropImageUrl,
                posterHeight: maxHeight
            )
            .foregroundStyle(.ultraThinMaterial)
            .frame(height: maxHeight)
            .opacity(1 - progress)
            
            VStack {
                Spacer()
                
                ZStack(alignment: .bottom) {
                    
                    Rectangle()
                        .fill(
                            .linearGradient(colors: [
                                .clear,
                                .clear,
                                .clear,
                                Color.background.opacity(0),
                                Color.background.opacity(0.8),
                                Color.background.opacity(0.97),
                                Color.background.opacity(0.98),
                                Color.background,
                            ], startPoint: .top, endPoint: .bottom)
                        )
                    
                    ShowInfoView(
                        onAddToLibraryClick: onAddToLibraryClick,
                        onWatchTrailerClick: onWatchTrailerClick
                    )
                    .opacity(1 + (progress > 0 ? -progress : progress))
                }
            }
        }
        .frame(height: maxHeight)
    }
    
    @ViewBuilder
    func ShowInfoView(
        onAddToLibraryClick : @escaping (Bool) -> Void,
        onWatchTrailerClick : @escaping (Int64) -> Void
    ) -> some View {
        
        VStack(spacing: 0){
            Text(show.title)
                .titleFont(size: 30)
                .foregroundColor(.text_color_bg)
                .lineLimit(1)
                .padding(.top, 8)
                .padding(.trailing, 16)
                .padding(.leading, 16)
            
            Text(show.overview)
                .bodyFont(size: 18)
                .foregroundColor(.text_color_bg)
                .lineLimit(3)
                .padding(.top, 1)
                .padding(.trailing, 16)
                .padding(.leading, 16)
            
            showDetailMetadata
            
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .center) {
                    ForEach(show.genres, id: \.self) { label in
                        ChipView(label: label)
                    }
                }
                .padding(.trailing, 16)
                .padding(.leading, 16)
            }
            .padding(.top, 8)
            
            showDetailButtons
                .padding(.top, 8)
        }
        
    }
    
    private var showDetailMetadata: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(alignment: .center) {
                
                if let status = show.status {
                    if status.isEmpty != true {
                        Text(status)
                            .bodyMediumFont(size: 14)
                            .foregroundColor(Color.accent)
                            .padding(3)
                            .background(Color.accent.opacity(0.12))
                            .cornerRadius(2)
                        
                        Text("•")
                            .bodyFont(size: 16)
                            .foregroundColor(.accent)
                    }
                }
                
                Text(show.year)
                    .bodyMediumFont(size: 16)
                    .foregroundColor(Color.text_color_bg)
                
                Text("•")
                    .bodyFont(size: 16)
                    .foregroundColor(.accent)
                
                if let language = show.language {
                    Text(language)
                        .bodyMediumFont(size: 16)
                        .textCase(.uppercase)
                        .foregroundColor(Color.text_color_bg)
                }
                
                Text("•")
                    .bodyFont(size: 16)
                    .foregroundColor(.accent)
                
                Text(String(format: "%.1f", show.rating))
                    .bodyMediumFont(size: 16)
                    .foregroundColor(Color.text_color_bg)
                
                Text("•")
                    .bodyFont(size: 16)
                    .foregroundColor(.accent)
                    .foregroundColor(Color.text_color_bg)
                
                
            }
        }
        .padding(.top, 5)
        .padding(.trailing, 16)
        .padding(.leading, 16)
    }
    private var showDetailButtons: some View {
        HStack(alignment: .center, spacing: 8) {
            
            BorderedButton(
                text: "Watch Trailer",
                systemImageName: "film.fill",
                color: .accent,
                borderColor: .accent,
                isOn: false,
                action: { onWatchTrailerClick(show.tmdbId) }
            )
            
            let followText = if (!show.isFollowed) { "Follow Show" } else { "Unfollow Show"}
            let buttonSystemImage = if (!show.isFollowed) { "plus.square.fill.on.square.fill" } else { "checkmark.square.fill"}
            
            BorderedButton(
                text: followText,
                systemImageName: buttonSystemImage,
                color: .accent,
                borderColor: .accent,
                isOn: false,
                action: { onAddToLibraryClick(show.isFollowed) }
            )
        }
        .padding(.bottom, 16)
        .padding(.top, 10)
    }
}
