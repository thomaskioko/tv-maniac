//
//  TobBar.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct TopBar: View {
    var onBackClicked: () -> Void
    
    var body: some View {
        VStack {
            HStack {
                Button("", action: onBackClicked )
                    .buttonStyle(CircleButtonStyle(imageName: "arrow.backward"))
                    .padding(.leading, 16)
                    .padding(.top, 60)
                Spacer()
            }
            Spacer()
        }
        .ignoresSafeArea()
    }
}

#Preview {
    TopBar(
        onBackClicked: {}
    )
}
