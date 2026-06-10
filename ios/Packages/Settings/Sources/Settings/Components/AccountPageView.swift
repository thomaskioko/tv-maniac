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
                .disabled(content.isProcessingAuth)
            }
            .padding(appTheme.spacing.medium)
        }
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
#endif
