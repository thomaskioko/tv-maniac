import SwiftUI
import TvManiac

struct AppThemeModifier: ViewModifier {
  let theme: DeveiceAppTheme
  @Environment(\.colorScheme) var systemTheme

  func body(content: Content) -> some View {
    content
      .environment(\.colorScheme, theme.overrideTheme ?? systemTheme)
  }
}

struct AppTintModifier: ViewModifier {
  let theme: DeveiceAppTheme

  func body(content: Content) -> some View {
    content
      .tint(theme.toAppThemeColor())
  }
}
