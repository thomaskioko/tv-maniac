//
//  ErrorUi.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.11.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct FullScreenView: View {
    
    var systemName: String
    var message: String
    
    
    var body: some View {
        
        VStack {
            Image(systemName: systemName)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(Color.accent)
                .font(Font.title.weight(.light))
                .frame(width: 120, height: 120)
                .padding(16)
            
            Text(message)
                .bodyMediumFont(size: 16)
                .padding(.top, 8)
                .padding(.trailing, 16)
                .padding(.leading, 16)
            
        }
        .frame(maxWidth: .infinity,maxHeight: .infinity)
        
    }
}
