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
    var headerHeight: CGFloat
    
    var body: some View {
        ZStack(alignment: .bottom) {
            HeaderCoverArtWorkView(
                backdropImageUrl: show.backdropImageUrl,
                posterHeight: headerHeight
            )
            .foregroundStyle(.ultraThinMaterial)
            .overlay(
                LinearGradient(
                    gradient: Gradient(colors: [
                        .clear,
                        .clear,
                        .clear,
                        Color.background.opacity(0),
                        Color.background.opacity(0.8),
                        Color.background.opacity(0.97),
                        Color.background,
                        Color.background,
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
            .frame(height: headerHeight)
            
            VStack {
                Spacer()
                ShowHeaderInfoView(show: show)
                    .opacity(1 - progress)
            }
            .frame(height: headerHeight)
        }
        .frame(height: headerHeight)
        .clipped()
    }
}


struct ShowHeaderInfoView: View {
    let show: ShowDetails
    
    var body: some View {
        VStack(spacing: 0) {
            Text(show.title)
                .titleFont(size: 30)
                .foregroundColor(.text_color_bg)
                .lineLimit(1)
                .padding(.top, 12)
                .padding([.leading, .trailing], 16)
            
            Text(show.overview)
                .font(.avenirNext(size: 17))
                .foregroundColor(.text_color_bg)
                .lineLimit(3)
                .padding(.top, 1)
                .padding(.trailing, 16)
                .padding(.leading, 16)
            
            showDetailMetadata
            
        }
        .padding(.bottom, 16)
    }
    
    private var showDetailMetadata: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(alignment: .center) {
                if let status = show.status, !status.isEmpty {
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
            }
            .padding(.horizontal)
        }
    }
}
