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

  public init(label: String) {
    self.label = label
  }

  public var body: some View {
    Text(label)
      .bodyMediumFont(size: 16)
      .foregroundColor(.accent)
      .padding([.leading, .trailing], 2)
      .padding(10)
      .background(Color.accent.opacity(0.12))
      .cornerRadius(5)
  }
}

#Preview {
  HStack {
    ChipView(label: "Drama")
    ChipView(label: "Action")
    ChipView(label: "Horror")
  }
}
