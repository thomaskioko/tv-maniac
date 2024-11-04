//
//  ErrorUi.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.11.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

public struct FullScreenView: View {
  private let systemName: String
  private let message: String
  private let buttonText: String?
  private let action: () -> Void

  public init(
    systemName: String = "exclamationmark.triangle.fill",
    message: String = "Something went wrong",
    buttonText: String? = nil,
    action: @escaping () -> Void = {}
  ) {
    self.systemName = systemName
    self.message = message
    self.buttonText = buttonText
    self.action = action
  }

  public var body: some View {
    VStack {
      Image(systemName: systemName)
        .resizable()
        .aspectRatio(contentMode: .fit)
        .foregroundColor(Color.accent)
        .font(Font.title.weight(.light))
        .frame(width: 120, height: 120)
        .padding(16)

      Text(message)
        .font(.avenirNext(size: 17))
        .padding([.leading, .trailing, .bottom], 8)

      if let buttonText = buttonText {
        FilledImageButton(
          text: buttonText,
          verticalPadding: 8,
          action: action
        )
        .background(Color.accent)
        .cornerRadius(5)
      }
    }
    .frame(maxWidth: .infinity, maxHeight: .infinity)
  }
}

#Preview {
  VStack {
    FullScreenView(
      systemName: "exclamationmark.triangle.fill",
      message: "Something went wrong"
    )
  }
}

#Preview {
  VStack {
    FullScreenView(
      systemName: "exclamationmark.triangle.fill",
      message: "Something went wrong",
      buttonText: "Retry"
    )
  }
}
