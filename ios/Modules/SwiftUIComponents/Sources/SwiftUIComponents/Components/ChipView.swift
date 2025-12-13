//
//  ChipView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct ChipView: View {
    @Theme private var theme
    private let label: String
    private let isSelected: Bool
    private let action: () -> Void

    public init(
        label: String,
        isSelected: Bool = false,
        action: @escaping () -> Void = {}
    ) {
        self.label = label
        self.isSelected = isSelected
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            Text(label)
                .textStyle(theme.typography.bodyMedium)
                .foregroundColor(theme.colors.secondary)
                .padding(.horizontal, theme.spacing.small)
                .padding(.vertical, theme.spacing.xSmall)
                .background(theme.colors.secondary.opacity(isSelected ? 0.24 : 0.08))
                .cornerRadius(theme.shapes.small)
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
