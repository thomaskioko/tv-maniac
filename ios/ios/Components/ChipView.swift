//
//  ChipView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct ChipView: View {
    var label: String
    
    var body: some View {
        Text(label)
            .bodyMediumFont(size: 16)
            .foregroundColor(Color.accent)
            .padding([.leading, .trailing], 2)
            .padding(10)
            .background(Color.accent.opacity(0.12))
            .cornerRadius(5)
    }
}
