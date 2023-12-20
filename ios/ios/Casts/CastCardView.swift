//
//  CastCardView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SDWebImageSwiftUI
import TvManiac

struct CastCardView: View {
    let cast: Casts
    
    var body: some View {
        VStack {
            if let profileUrl = cast.profileUrl {
                WebImage(url: URL(string: profileUrl))
                    .resizable()
                    .placeholder { profilePlaceholder }
                    .aspectRatio(contentMode: .fill)
                    .frame(
                        width: DimensionConstants.profileWidth,
                        height: DimensionConstants.profileHeight
                    )
                    .clipShape(
                        RoundedRectangle(
                            cornerRadius: DimensionConstants.cornerRadius,
                            style: .continuous
                        )
                    )
                    .shadow(radius: DimensionConstants.shadowRadius)
                    .overlay {
                        ZStack(alignment: .bottom) {
                            
                            Color.black.opacity(0.2)
                                .frame(height: 40)
                                .mask {
                                    Color.imageGradient
                                }
                            Rectangle()
                                .fill(.ultraThinMaterial)
                                .frame(height: 80)
                                .mask {
                                    VStack(spacing: 0) {
                                        Color.imageGradient
                                        .frame(height: 60)
                                        
                                        Rectangle()
                                    }
                                }
                            name
                            
                            
                        }
                        .frame(width: DimensionConstants.profileWidth,
                               height: DimensionConstants.profileHeight)
                        .clipShape(
                            RoundedRectangle(
                                cornerRadius: DimensionConstants.cornerRadius,
                                style: .continuous
                            )
                        )
                    }
                    .transition(.opacity)
            } else {
                profilePlaceholder
            }
            
        }
    }
    
    private var profilePlaceholder: some View {
        ZStack {
            ZStack {
                Rectangle().fill(.gray.gradient)
                Image(systemName: "person")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 50, height: 50, alignment: .center)
                    .foregroundColor(.white)
                name
            }
            .frame(width: DimensionConstants.profileWidth,
                   height: DimensionConstants.profileHeight)
            .clipShape(
                RoundedRectangle(
                    cornerRadius: DimensionConstants.cornerRadius,
                    style: .continuous
                )
            )
        }
    }
    
    private var name: some View {
        VStack {
            Spacer()
            HStack {
                Text(cast.name)
                    .font(.callout)
                    .foregroundColor(.white)
                    .fontWeight(.semibold)
                    .lineLimit(DimensionConstants.lineLimit)
                    .padding(.leading, 6)
                Spacer()
            }
            HStack {
                Text(cast.characterName)
                    .foregroundColor(.white)
                    .font(.caption)
                    .lineLimit(DimensionConstants.lineLimit)
                    .padding(.leading, 6)
                Spacer()
            }
        }
        .padding(.bottom)
    }
}


private struct DimensionConstants {
    static let profileWidth: CGFloat = 120
    static let profileHeight: CGFloat = 160
    static let shadowRadius: CGFloat = 2.5
    static let cornerRadius: CGFloat = 4
    static let lineLimit: Int = 1
}
