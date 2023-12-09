//
//  EmptyView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 04.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct EmptyUIView: View {
    var body: some View {
        VStack{
            Spacer()
            
            
            Text("🚧")
                .titleBoldFont(size: 73)
                .font(.title3)
                .padding(16)
            
            Text("Construction In progress!!")
                .titleBoldFont(size: 34)
                .font(.title3)
                .frame(maxWidth: .infinity)
            
            
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    EmptyUIView()
}
