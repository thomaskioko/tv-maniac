import SwiftUI

public struct ProviderButton: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.isEnabled) private var isEnabled

    private let title: String
    private let logo: String
    private let action: () -> Void

    public init(title: String, logo: String, action: @escaping () -> Void) {
        self.title = title
        self.logo = logo
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: theme.spacing.small) {
                Image(logo, bundle: .module)
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 24, height: 24)
                Text(title)
                    .textStyle(theme.typography.labelLarge)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, theme.spacing.medium)
            .foregroundStyle(.appOnSurface)
            .overlay(
                Capsule().stroke(theme.colors.onSurface.opacity(0.4), lineWidth: 2)
            )
            .contentShape(Capsule())
        }
        .buttonStyle(.plain)
        .opacity(isEnabled ? 1 : 0.5)
    }
}

#if DEBUG
    #Preview {
        VStack(spacing: 12) {
            ProviderButton(title: "Continue with Trakt", logo: "TraktMono", action: {})
            ProviderButton(title: "Continue with Simkl", logo: "SimklMono", action: {})
        }
        .padding()
        .appPreview()
    }
#endif
