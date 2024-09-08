import Foundation
import SwiftUI

public extension Font {
  static func WorkSansBlack(size: CGFloat) -> Font {
    Font.custom("WorkSans-Black", size: size)
  }

  static func WorkSansBold(size: CGFloat) -> Font {
    Font.custom("WorkSans-Bold", size: size).weight(.regular)
  }

  static func WorkSansExtraBold(size: CGFloat) -> Font {
    Font.custom("WorkSans-ExtraBold", size: size)
  }

  static func WorkSansLight(size: CGFloat) -> Font {
    Font.custom("WorkSans-Light", size: size)
  }

  static func WorkSansExtraLight(size: CGFloat) -> Font {
    Font.custom("WorkSans-ExtraLight", size: size)
  }

  static func WorkSansMedium(size: CGFloat) -> Font {
    Font.custom("WorkSans-Medium", size: size)
  }

  static func WorkSansRegular(size: CGFloat) -> Font {
    Font.custom("WorkSans-Regular", size: size)
  }

  static func WorkSansSemiBold(size: CGFloat) -> Font {
    Font.custom("WorkSans-Semibold", size: size)
  }

  static func WorkSansThin(size: CGFloat) -> Font {
    Font.custom("WorkSans-Thin", size: size)
  }

  static func avenirNext(size: Int) -> Font {
    return Font.custom("Avenir Next", size: CGFloat(size))
  }

  static func avenirNextRegular(size: Int) -> Font {
    return Font.custom("AvenirNext-Regular", size: CGFloat(size))
  }
}

struct TitleFont: ViewModifier {
  let size: CGFloat

  func body(content: Content) -> some View {
    content.font(.WorkSansBlack(size: size))
  }
}

struct TitleBlackFont: ViewModifier {
  let size: CGFloat

  func body(content: Content) -> some View {
    content.font(.WorkSansSemiBold(size: size))
  }
}

struct TitleSemiBoldFont: ViewModifier {
  let size: CGFloat

  func body(content: Content) -> some View {
    content.font(.WorkSansBlack(size: size))
  }
}

struct TitleBoldFont: ViewModifier {
  let size: CGFloat

  func body(content: Content) -> some View {
    content.font(.WorkSansBold(size: size))
  }
}

struct BodyFont: ViewModifier {
  let size: CGFloat

  func body(content: Content) -> some View {
    content.font(.WorkSansRegular(size: size))
  }
}

struct BodyMediumFont: ViewModifier {
  let size: CGFloat

  func body(content: Content) -> some View {
    return content.font(.WorkSansMedium(size: size))
  }
}

struct BodyThinFont: ViewModifier {
  let size: CGFloat

  func body(content: Content) -> some View {
    return content.font(.WorkSansThin(size: size))
  }
}

struct CaptionFont: ViewModifier {
  let size: CGFloat

  func body(content: Content) -> some View {
    content.font(.WorkSansLight(size: size))
  }
}

public extension View {
  func titleFont(size: CGFloat) -> some View {
    ModifiedContent(content: self, modifier: TitleFont(size: size))
  }

  func titleBoldFont(size: CGFloat) -> some View {
    ModifiedContent(content: self, modifier: TitleBoldFont(size: size))
  }

  func titleSemiBoldFont(size: CGFloat) -> some View {
    ModifiedContent(content: self, modifier: TitleSemiBoldFont(size: size))
  }

  func titleStyle() -> some View {
    ModifiedContent(content: self, modifier: TitleFont(size: 16))
  }

  func bodyFont(size: CGFloat) -> some View {
    ModifiedContent(content: self, modifier: BodyFont(size: size))
  }

  func bodyThinFont(size: CGFloat) -> some View {
    ModifiedContent(content: self, modifier: BodyFont(size: size))
  }


  func bodyMediumFont(size: CGFloat) -> some View {
    ModifiedContent(content: self, modifier: BodyMediumFont(size: size))
  }

  func captionStyle(size: CGFloat) -> some View {
    ModifiedContent(content: self, modifier: CaptionFont(size: size))
  }

  func captionFont(size: CGFloat) -> some View {
    ModifiedContent(content: self, modifier: CaptionFont(size: size))
  }
}
