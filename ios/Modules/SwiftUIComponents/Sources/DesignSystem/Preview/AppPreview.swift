import SwiftUI

public extension View {
    func appPreview(_ theme: TvManiacTheme = LightTheme()) -> some View {
        appTheme(theme)
            .background(theme.colors.background)
    }
}
