import SwiftUI

public struct FilledImageButton: View {
  private let text: String
  private let systemImageName: String?
  private let color: Color
  private let textColor: Color
  private let borderColor: Color
  private let cornerRadius: CGFloat
  private let action: () -> Void

  public init(
    text: String,
    systemImageName: String? = nil,
    color: Color = .accent,
    textColor: Color = .white,
    borderColor: Color = .accent,
    cornerRadius: CGFloat = 5,
    action: @escaping () -> Void
  ) {
    self.text = text
    self.systemImageName = systemImageName
    self.color = color
    self.textColor = textColor
    self.borderColor = borderColor
    self.cornerRadius = cornerRadius
    self.action = action
  }

  public var body: some View {
    TvManiacButton(
      text: text,
      color: color,
      textColor: .white,
      borderColor: borderColor,
      systemImageName: systemImageName,
      action: action
    )
    .background(
      RoundedRectangle(cornerRadius: cornerRadius)
      .foregroundColor(color)
    )
  }
}

#Preview {
  FilledImageButton(
    text: "Watch Trailer",
    systemImageName: "film",
    action: {}
  )
}
