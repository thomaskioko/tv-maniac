import SwiftUI

public extension View {
  func appTheme() -> some View {
    modifier(AppThemeModifier())
  }

  func appTint() -> some View {
    modifier(AppTintModifier())
  }
}
