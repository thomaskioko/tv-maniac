//
//  EmptyView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 04.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct EmptyUIView: View {
  private let title: String
  private let subtitle: String?

  public init(
    title: String,
    subtitle: String? = nil
  ) {
    self.title = title
    self.subtitle = subtitle
  }

  public var body: some View {
    VStack {
      Spacer()

      Text("🚧")
        .titleBoldFont(size: 78)
        .padding(16)

      Text(title)
        .bodyFont(size: 28)
        .frame(maxWidth: .infinity)

      if let text = subtitle {
        Text(text)
          .font(.callout)
          .frame(maxWidth: .infinity)
      }

      Spacer()
    }
    .frame(maxWidth: .infinity, maxHeight: .infinity)
    .padding(.horizontal)
  }
}

#Preview {
  EmptyUIView(
    title: "Construction In progress!!",
    subtitle: "Please wait we are wokfing on this!"
  )
}
