//
// Created by Thomas Kioko on 07.04.22.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct TopNavBar: View {
    
    var titleProgress: CGFloat
    var title: String
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var body: some View {
        
        HStack(spacing: 15) {
            
            Button {
                presentationMode.wrappedValue.dismiss()
            } label: {
                Image(systemName: "chevron.left")
                    .font(.body.bold())
                    .foregroundColor(.white)
                    .padding([.top, .bottom,.trailing])
            }
            
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
