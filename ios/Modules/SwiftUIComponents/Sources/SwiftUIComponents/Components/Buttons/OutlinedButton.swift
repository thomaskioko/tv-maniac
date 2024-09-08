import SwiftUI

public struct OutlinedButton: View {
  private let text: String
  private let systemImageName: String?
  private let color: Color
  private let textColor: Color
  private let borderColor: Color
  private let action: () -> Void

  public init(
    text: String,
    systemImageName: String?,
    color: Color = .accent,
    textColor: Color = .white,
    borderColor: Color = .accent,
    action: @escaping () -> Void
  ) {
    self.text = text
    self.systemImageName = systemImageName
    self.color = color
    self.textColor = textColor
    self.borderColor = borderColor
    self.action = action
  }

  public var body: some View {
    TvManiacButton(
      text: text,
      color: color,
      textColor: color,
      borderColor: borderColor,
      systemImageName: systemImageName,
      action: action
    )
    .overlay(
      RoundedRectangle(cornerRadius: 5)
        .stroke(borderColor, lineWidth: 2)
    )
  }
}

#Preview {
  OutlinedButton(
    text: "Watch Trailer",
    systemImageName: "film",
    action: {}
  )
}
