//
//  ChipView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import DesignSystem
import SwiftUI

public struct ChipView: View {
    @Environment(\.appTheme) private var theme
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
                .foregroundStyle(.appSecondary)
                .padding(.horizontal, theme.spacing.small)
                .padding(.vertical, theme.spacing.xSmall)
                .background(.appSecondary.opacity(isSelected ? 0.24 : 0.08))
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
