//
//  BottomTabView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/21/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct BottomTabView: View {
    let title: String
    let systemImage: String
    let isActive: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Image(systemName: systemImage)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 26, height: 26)
                Text(title)
                    .bodyMediumFont(size: 14)
            }
            .foregroundColor(isActive ? .blue : .text_color_bg)
            .opacity(isActive ? 1 : 0.5)
        }
        .buttonStyle(PlainButtonStyle())
    }
}
