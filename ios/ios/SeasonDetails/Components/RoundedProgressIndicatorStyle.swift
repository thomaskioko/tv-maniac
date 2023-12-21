//
//  RoundedProgressIndicatorStyle.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct RoundedRectProgressViewStyle: ProgressViewStyle {
    var progressIndicatorHeight: CGFloat = 8
    
    
    func makeBody(configuration: Configuration) -> some View {
        ZStack(alignment: .leading) {
            Rectangle()
                .frame(height: progressIndicatorHeight)
                .foregroundColor(.accent.opacity(0.2))
                .overlay(Color.accent.opacity(0.2))
            
            Rectangle()
                .frame(
                    width: CGFloat(configuration.fractionCompleted ?? 0) * DimensionConstants.screenWidth,
                    height: progressIndicatorHeight
                )
                .foregroundColor(.accent)
        }
    }
}

private struct DimensionConstants {
    static let screenWidth = UIScreen.main.bounds.size.width
}

#Preview {
    VStack {
        Spacer()
        ProgressView(value: CGFloat(0.4), total: 1)
            .progressViewStyle(
                RoundedRectProgressViewStyle(progressIndicatorHeight: 6)
            )
        Spacer()
    }
}
