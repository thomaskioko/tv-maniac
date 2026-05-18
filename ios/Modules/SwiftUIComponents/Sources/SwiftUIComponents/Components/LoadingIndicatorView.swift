import DesignSystem
import SwiftUI

public struct LoadingIndicatorView: View {
    @Environment(\.appTheme) private var theme

    public init() {}

    public var body: some View {
        ZStack {
            Spacer()

            ProgressView()
                .progressViewStyle(CircularProgressViewStyle())
                .scaleEffect(1.5)
                .tint(theme.colors.accent)
                .padding(.horizontal)
                .padding(.bottom, theme.spacing.xSmall)

            Spacer()
        }
        .padding(theme.spacing.medium)
        .edgesIgnoringSafeArea(.all)
    }
}

#Preview {
    LoadingIndicatorView()
}
