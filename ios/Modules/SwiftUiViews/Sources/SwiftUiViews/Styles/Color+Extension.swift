import SwiftUI

public extension Color {
  static let accent = Color(.accent)
  static let background = Color(.background)
  static let blue = Color(.blue)
  static let gradient_background = Color(.background)
  static let content_background = Color(.contentBackground)
  static let textColorBg = Color(.textColor)
  static let grey200 = Color(.grey200)

  static var linearGradient = LinearGradient(
    gradient: Gradient(stops: [
      .init(color: .gradient_background, location: 0),
      .init(color: .clear, location: 0.8)
    ]),
    startPoint: .bottom,
    endPoint: .top
  )

  static var imageGradient = LinearGradient(
    colors: [
      Color.black.opacity(0),
      Color.black.opacity(0.383),
      Color.black.opacity(0.707),
      Color.black.opacity(0.924),
      Color.black
    ],
    startPoint: .top,
    endPoint: .bottom
  )

  static var iosBlue: Color {
    Color(red: 0/255, green: 122/255, blue: 255/255)
  }
}
