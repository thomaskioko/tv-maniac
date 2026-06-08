import DesignSystem
import SwiftUI

struct InfoPageView: View {
    @Environment(\.appTheme) private var appTheme
    private let content: SettingsInfoContent

    init(content: SettingsInfoContent) {
        self.content = content
    }

    var body: some View {
        VStack(spacing: appTheme.spacing.large) {
            VStack(spacing: appTheme.spacing.medium) {
                content.icon
                    .resizable()
                    .scaledToFit()
                    .frame(width: 72, height: 72)
                    .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.large))

                Text(content.appName)
                    .textStyle(appTheme.typography.headlineSmall)
                    .foregroundColor(appTheme.colors.onSurface)

                Text(content.versionText)
                    .textStyle(appTheme.typography.bodyLarge)
                    .foregroundColor(appTheme.colors.secondary)
                    .onTapGesture(perform: content.onVersionTap)
            }
            .frame(maxWidth: .infinity)
            .padding(.top, appTheme.spacing.large)

            Text(content.description)
                .textStyle(appTheme.typography.bodyMedium)
                .foregroundColor(appTheme.colors.onSurfaceVariant)
                .multilineTextAlignment(.center)

            SettingsCard {
                SettingsLinkRow(
                    SettingsLinkItem(
                        id: "source",
                        title: content.sourceCodeLabel,
                        body: content.sourceCodeValue,
                        link: content.sourceCodeValue,
                        onOpen: content.onSourceCodeTap
                    )
                )
            }

            Text(content.apiDisclaimer)
                .textStyle(appTheme.typography.bodySmall)
                .foregroundColor(appTheme.colors.onSurfaceVariant)
                .multilineTextAlignment(.center)
        }
    }
}

#if DEBUG
#Preview {
    InfoPageView(content: SettingsPreviewSamples.infoContent)
        .padding()
        .appPreview()
}
#endif
