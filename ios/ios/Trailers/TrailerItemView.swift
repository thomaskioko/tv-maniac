//
//  TrailerItemView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SDWebImageSwiftUI
import YouTubePlayerKit
import TvManiac

struct TrailerItemView: View {
    
    private let openInYouTube: Bool
    private let trailer: Trailer
    private let player: YouTubePlayer
    
    
    @State private var isLoading = false
    
    
    init(openInYouTube: Bool, trailer: Trailer) {
        self.openInYouTube = openInYouTube
        self.trailer = trailer

        self.player = YouTubePlayer(
            source: .video(id: trailer.key),
            configuration: .init(
                automaticallyAdjustsContentInsets: true,
                allowsPictureInPictureMediaPlayback: false,
                fullscreenMode: .system,
                autoPlay: false,
                showControls: true,
                useModestBranding: true,
                playInline: false,
                showRelatedVideos: false
            )
        )
    }
    
    var body: some View {
        ZStack {
            YouTubePlayerView(player)
                .frame(width: DimensionConstants.imageWidth,
                       height: DimensionConstants.imageHeight)
                .opacity(0)
            
            VStack {
                WebImage(url: URL(string: trailer.youtubeThumbnailUrl))
                    .resizable()
                    .placeholder {
                        placeholder
                    }
                    .aspectRatio(contentMode: .fill)
                    .transition(.opacity)
                    .frame(width: DimensionConstants.imageWidth,
                           height: DimensionConstants.imageHeight)
                    .clipShape(RoundedRectangle(cornerRadius: DimensionConstants.imageRadius,
                                                style: .continuous))
                    .overlay { overlay }
                    .shadow(radius: 2.5)
                
                HStack {
                    Text(trailer.name)
                        .lineLimit(DimensionConstants.lineLimits)
                        .padding([.trailing])
                        .bodyMediumFont(size: 16)
                        .foregroundColor(.text_color_bg)
                    Spacer()
                }
            }
            .frame(width: DimensionConstants.imageWidth)
        }
        .frame(width: DimensionConstants.imageWidth)
        .accessibilityElement(children: .combine)
        .accessibilityLabel(trailer.name)
        .onTapGesture(perform: openVideo)
    
    }
    
    private var placeholder: some View {
        ZStack {
            Color.secondary
            Image(systemName: "play.fill")
                .foregroundColor(.white)
                .imageScale(.medium)
        }
        .transition(.opacity)
        .frame(width: DimensionConstants.imageWidth,
               height: DimensionConstants.imageHeight)
        .clipShape(RoundedRectangle(cornerRadius: DimensionConstants.imageRadius,
                                    style: .continuous))
    }
    
    private var overlay: some View {
        ZStack {
            Color.black.opacity(DimensionConstants.overlayOpacity)
            if isLoading {
                ProgressView()
                    .tint(.white)
                    .frame(width: DimensionConstants.overlayWidth,
                           height: DimensionConstants.overlayHeight,
                           alignment: .center)
                    .padding()
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 4) {
                            withAnimation { self.isLoading = false }
                        }
                    }
            } else {
                Image(systemName: "play.circle.fill")
                    .resizable()
                    .frame(width: DimensionConstants.overlayWidth,
                           height: DimensionConstants.overlayHeight,
                           alignment: .center)
                    .symbolRenderingMode(.palette)
                    .foregroundStyle(.white, .secondary)
                    .scaledToFit()
                    .imageScale(.medium)
                    .padding()
            }

        }
        .frame(width: DimensionConstants.imageWidth,
               height: DimensionConstants.imageHeight)
        .clipShape(RoundedRectangle(cornerRadius: DimensionConstants.imageRadius,
                                    style: .continuous))
    }
    
    private func openVideo() {
        if openInYouTube {
            if let url = urlBuilder(path: trailer.key) {
                UIApplication.shared.open(url)
            }
        } else {
            self.isLoading = true
            player.play()
        }
    }
}

private func urlBuilder(path: String? = nil) -> URL? {
    guard let path else { return nil }
    var components = URLComponents()
    components.scheme = "https"
    components.host = "www.youtube.com"
    components.path = "/embed/\(path)"
    return components.url
}

private struct DimensionConstants {
    static let imageRadius: CGFloat = 8
    static let imageShadow: CGFloat = 2.5
    static let imageWidth: CGFloat = 260
    static let imageHeight: CGFloat = 140
    static let overlayOpacity: Double = 0.2
    static let overlayWidth: CGFloat = 50
    static let overlayHeight: CGFloat = 50
    static let lineLimits: Int = 1
}

