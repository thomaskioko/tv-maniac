import SwiftUI

public struct FilledImageButton: View {
  private let text: String
  private let systemImageName: String?
  private let color: Color
  private let textColor: Color
  private let borderColor: Color
  private let cornerRadius: CGFloat
  private let verticalPadding: CGFloat
  private let action: () -> Void

  public init(
    text: String,
    systemImageName: String? = nil,
    color: Color = .accent,
    textColor: Color = .white,
    borderColor: Color = .accent,
    cornerRadius: CGFloat = 5,
    verticalPadding: CGFloat = 16,
    action: @escaping () -> Void
  ) {
    self.text = text
    self.systemImageName = systemImageName
    self.color = color
    self.textColor = textColor
    self.borderColor = borderColor
    self.cornerRadius = cornerRadius
    self.verticalPadding = verticalPadding
    self.action = action
  }

  public var body: some View {
    TvManiacButton(
      text: text,
      color: color,
      textColor: .white,
      borderColor: borderColor,
      systemImageName: systemImageName,
      verticalPadding: verticalPadding,
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
