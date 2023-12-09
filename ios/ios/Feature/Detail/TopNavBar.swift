//
// Created by Thomas Kioko on 07.04.22.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct TopNavBar: View {
    
    var titleProgress: CGFloat
    var title: String
    let action: () -> Void
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var body: some View {
        
        HStack(spacing: 15) {
            
            Button(action: action) {
                Image(systemName: "arrow.backward.circle.fill")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 32, height: 34)
                    .font(.body.bold())
                    .foregroundColor(.white)
                    .padding([.top, .bottom,.trailing])
            }
            .symbolVariant(.circle.fill)
            
            Spacer(minLength: 0)
            
        }
        .overlay(content: {
            Text(title)
                .titleFont(size: 24)
                .foregroundColor(Color.text_color_bg)
                .lineLimit(1)
                .offset(y: -titleProgress > 0.75 ? 0 : 45)
                .clipped()
                .animation(.easeInOut(duration: 0.25), value: -titleProgress > 0.75)
        })
        .padding(.top, 10)
        
    }
}
