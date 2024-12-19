import SwiftUI

public extension View {
  func appTheme(_ theme: DeveiceAppTheme) -> some View {
    modifier(AppThemeModifier(theme: theme))
  }

  func appTint(_ theme: DeveiceAppTheme) -> some View {
    modifier(AppTintModifier(theme: theme))
  }
}
