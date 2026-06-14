import Components
import DesignSystem
import Models
import SwiftUI

struct AccountPageView: View {
    @Environment(\.appTheme) private var appTheme
    private let content: SettingsAccountContent

    init(content: SettingsAccountContent) {
        self.content = content
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.xSmall) {
            SettingsSectionLabel(content.isAuthenticated ? content.authenticationLabel : content.connectTitle)

            if content.isAuthenticated {
                connectedCard
            } else {
                providerList
            }
        }
    }

    private var connectedCard: some View {
        SettingsCard {
            VStack(alignment: .leading, spacing: appTheme.spacing.medium) {
                HStack(spacing: appTheme.spacing.medium) {
                    Image(content.providerLogoName, bundle: .designSystem)
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 28, height: 28)
                        .foregroundColor(appTheme.colors.onSurface)
                    VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                        Text(content.providerName)
                            .textStyle(appTheme.typography.titleMedium)
                            .foregroundColor(appTheme.colors.onSurface)
                        Text(content.connectedTitle)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                        Text(content.connectedDescription)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                    }
                    Spacer(minLength: 0)
                }

                HStack(spacing: appTheme.spacing.small) {
                    logoutButton

                    if let switchLabel = content.switchActionLabel {
                        switchButton(label: switchLabel)
                    }
                }
            }
            .padding(appTheme.spacing.medium)
        }
    }

    private var logoutButton: some View {
        Button(action: content.onLogout) {
            Group {
                if content.isProcessingAuth {
                    ProgressView()
                        .progressViewStyle(.circular)
                        .tint(appTheme.colors.onSecondary)
                } else {
                    Text(content.logoutLabel)
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
        .disabled(content.isProcessingAuth || content.isSwitching)
    }

    private func switchButton(label: String) -> some View {
        Button(action: content.onSwitchProvider) {
            Group {
                if content.isSwitching {
                    HStack(spacing: appTheme.spacing.xxSmall) {
                        ProgressView()
                            .progressViewStyle(.circular)
                            .tint(appTheme.colors.secondary)
                        Text(content.switchingLabel)
                            .textStyle(appTheme.typography.labelLarge)
                            .foregroundColor(appTheme.colors.secondary)
                    }
                } else {
                    HStack(spacing: appTheme.spacing.xxSmall) {
                        if let logoName = content.switchTargetLogoName {
                            Image(logoName, bundle: .designSystem)
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .frame(width: 16, height: 16)
                                .foregroundColor(appTheme.colors.secondary)
                        }
                        Text(label)
                            .textStyle(appTheme.typography.labelLarge)
                            .foregroundColor(appTheme.colors.secondary)
                    }
                }
            }
            .padding(.horizontal, appTheme.spacing.large)
            .padding(.vertical, appTheme.spacing.small)
            .background(appTheme.colors.secondary.opacity(0.12))
            .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.medium))
        }
        .buttonStyle(.plain)
        .disabled(content.isSwitching || content.isProcessingAuth)
    }

    private var providerList: some View {
        ProviderSignInCard(
            title: content.authenticationLabel,
            description: content.syncDescription,
            providers: content.authProviders,
            onProviderSelected: content.onProviderSelected
        )
    }
}

#if DEBUG
    #Preview("Connected") {
        AccountPageView(content: SettingsPreviewSamples.accountContent(authenticated: true))
            .padding()
            .appPreview()
    }

    #Preview("Logged Out") {
        AccountPageView(content: SettingsPreviewSamples.accountContent(authenticated: false))
            .padding()
            .appPreview()
    }

    #Preview("Switch Affordance") {
        AccountPageView(content: SettingsPreviewSamples.accountContent(authenticated: true, withSwitchAffordance: true))
            .padding()
            .appPreview()
    }

    #Preview("Switching In Progress") {
        AccountPageView(content: SettingsPreviewSamples.accountContent(authenticated: true, isSwitching: true))
            .padding()
            .appPreview()
    }

    #Preview("Switch Confirm Dialog") {
        AccountPageView(content: SettingsPreviewSamples.accountContent(authenticated: true, showSwitchConfirmation: true))
            .padding()
            .appPreview()
    }
#endif
