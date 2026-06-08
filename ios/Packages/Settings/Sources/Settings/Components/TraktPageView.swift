import DesignSystem
import SwiftUI

struct TraktPageView: View {
    @Environment(\.appTheme) private var appTheme
    private let content: SettingsTraktContent

    init(content: SettingsTraktContent) {
        self.content = content
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            SettingsCard {
                HStack(spacing: appTheme.spacing.medium) {
                    Image("TraktLogo", bundle: .module)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 48, height: 48)
                    VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                        Text(content.title)
                            .textStyle(appTheme.typography.titleMedium)
                            .foregroundColor(appTheme.colors.onSurface)
                        Text(content.description)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                    }
                    Spacer(minLength: 0)
                }
                .padding(appTheme.spacing.medium)
            }

            VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                SettingsSectionLabel(content.authenticationLabel)
                SettingsCard {
                    VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                        Text(content.connectedTitle)
                            .textStyle(appTheme.typography.bodyLarge)
                            .foregroundColor(appTheme.colors.onSurface)
                        Text(content.connectedDescription)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                        Button(action: content.isAuthenticated ? content.onLogout : content.onLogin) {
                            Group {
                                if content.isProcessingAuth {
                                    ProgressView()
                                        .progressViewStyle(.circular)
                                        .tint(appTheme.colors.onSecondary)
                                } else {
                                    Text(content.isAuthenticated ? content.logoutLabel : content.loginLabel)
                                        .textStyle(appTheme.typography.labelLarge)
                                        .foregroundColor(appTheme.colors.onSecondary)
                                }
                            }
                            .padding(.horizontal, appTheme.spacing.large)
                            .padding(.vertical, appTheme.spacing.small)
                            .background(appTheme.colors.secondary)
                            .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.medium))
                        }
                        .buttonStyle(.plain)
                        .disabled(content.isProcessingAuth)
                    }
                    .padding(appTheme.spacing.medium)
                }
            }
        }
    }
}

#if DEBUG
#Preview("Connected") {
    TraktPageView(content: SettingsPreviewSamples.traktContent(authenticated: true))
        .padding()
        .appPreview()
}

#Preview("Logged Out") {
    TraktPageView(content: SettingsPreviewSamples.traktContent(authenticated: false))
        .padding()
        .appPreview()
}
#endif
