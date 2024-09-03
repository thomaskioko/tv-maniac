import SwiftUI

public extension UIColor {
  static let accentYellow: UIColor = #colorLiteral(red: 0.9921568627, green: 0.7803921569, blue: 0.01568627451, alpha: 1) // #FDC704
  static let accentBlue: UIColor = #colorLiteral(red: 0.03921568627, green: 0.2941176471, blue: 1, alpha: 1) // #0A4BFF
  static let backgroundLight: UIColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) // #00000
  static let backgroundDark: UIColor = #colorLiteral(red: 0.1725490196, green: 0.1725490196, blue: 0.1882352941, alpha: 1) // #2C2C30
  static let grey200: UIColor = #colorLiteral(red: 0.2549019608, green: 0.2549019608, blue: 0.2549019608, alpha: 1) // #414141
  static let greyLight: UIColor = #colorLiteral(red: 0.9058823529, green: 0.8784313725, blue: 0.9254901961, alpha: 1) // #49454F
  static let greyDark: UIColor = #colorLiteral(red: 0.2862745098, green: 0.2705882353, blue: 0.3098039216, alpha: 1) // #E7E0EC
  static let textColorLight: UIColor = #colorLiteral(red: 0.09803921569, green: 0.09803921569, blue: 0.09803921569, alpha: 1) // #191919
  static let textColorDark: UIColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) // #FFFFFF

  static let accent = UIColor(light: .accentBlue, dark: .accentYellow)
  static let background = UIColor(light: .backgroundLight, dark: .backgroundLight)
  static let contentBackground = UIColor(light: .greyLight, dark: .greyDark)
  static let textColor = UIColor(light: .textColorLight, dark: .textColorDark)

  /// Returns a color object that generates its color data dynamically based on the current user
  /// interface style.
  ///
  /// - Parameter light: The color for the light interface style.
  /// - Parameter dark : The color for the dark interface style.
  ///
  convenience init(light: UIColor, dark: UIColor) {
    self.init { $0.userInterfaceStyle == .dark ? dark : light }
  }
}