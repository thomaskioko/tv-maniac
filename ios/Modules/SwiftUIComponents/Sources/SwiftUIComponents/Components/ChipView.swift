//
//  ChipView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct ChipView: View {
    private let label: String
    private let action: () -> Void

    public init(
        label: String,
        action: @escaping () -> Void = {}
    ) {
        self.label = label
        self.action = action
    }

    public var body: some View {
        Button(
            action: action
        ) {
            Text(label)
                .bodyMediumFont(size: 16)
                .foregroundColor(.accent)
                .padding([.leading, .trailing], 2)
                .padding(10)
                .background(Color.accent.opacity(0.12))
                .cornerRadius(5)
        }
    }
}

#Preview {
    HStack {
        ChipView(label: "Drama")
        ChipView(label: "Action")
        ChipView(label: "Horror")
    }
}
