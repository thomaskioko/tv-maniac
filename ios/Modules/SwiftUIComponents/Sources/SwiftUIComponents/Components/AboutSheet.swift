import Components
import DesignSystem
import Models
import SwiftUI

public struct AboutSheet: View {
    @Environment(\.appTheme) private var theme

    private let appName: String
    private let versionText: String
    private let aboutTitle: String
    private let aboutDescription: String
    private let sourceCodeLabel: String
    private let sourceCodeAction: String
    private let apiDisclaimer: String
    private let icon: Image
    private let onVersionTap: () -> Void
    private let onSourceCodeTap: () -> Void

    public init(
        appName: String,
        versionText: String,
        aboutTitle: String,
        aboutDescription: String,
        sourceCodeLabel: String,
        sourceCodeAction: String,
        apiDisclaimer: String,
        icon: Image,
        onVersionTap: @escaping () -> Void,
        onSourceCodeTap: @escaping () -> Void
    ) {
        self.appName = appName
        self.versionText = versionText
        self.aboutTitle = aboutTitle
        self.aboutDescription = aboutDescription
        self.sourceCodeLabel = sourceCodeLabel
        self.sourceCodeAction = sourceCodeAction
        self.apiDisclaimer = apiDisclaimer
        self.icon = icon
        self.onVersionTap = onVersionTap
        self.onSourceCodeTap = onSourceCodeTap
    }

    public var body: some View {
        ZStack(alignment: .bottom) {
            ScrollView {
                VStack(spacing: 0) {
                    appIdentitySection
                    themedDivider
                    aboutSection
                    themedDivider
                    sourceCodeRow
                    themedDivider

                    Spacer()
                        .frame(height: theme.spacing.xxLarge + theme.spacing.large)
                }
            }

            Text(apiDisclaimer)
                .textStyle(theme.typography.labelSmall)
                .foregroundStyle(.appOnSurfaceVariant)
                .multilineTextAlignment(.center)
                .padding(theme.spacing.large)
                .frame(maxWidth: .infinity)
                .background(.appSurface)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(.appSurface)
        .tint(theme.colors.accent)
        .presentationDetents([.large])
    }

    private var appIdentitySection: some View {
        VStack(spacing: theme.spacing.medium) {
            icon
                .resizable()
                .scaledToFit()
                .frame(width: 72, height: 72)
                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large))

            Text(appName)
                .textStyle(theme.typography.headlineMedium)
                .foregroundStyle(.appOnSurface)

            Text(versionText)
                .textStyle(theme.typography.bodyMedium)
                .foregroundStyle(.appOnSurfaceVariant)
                .contentShape(Rectangle())
                .onTapGesture(perform: onVersionTap)
        }
        .padding(.vertical, theme.spacing.xLarge)
    }

    private var aboutSection: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
            Text(aboutTitle)
                .textStyle(theme.typography.titleMedium)
                .foregroundStyle(.appOnSurface)

            Text(aboutDescription)
                .textStyle(theme.typography.bodyMedium)
                .foregroundStyle(.appOnSurfaceVariant)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(theme.spacing.medium)
    }

    private var sourceCodeRow: some View {
        Button(action: onSourceCodeTap) {
            HStack {
                Text(sourceCodeLabel)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(.appOnSurface)
                Spacer()
                Text(sourceCodeAction)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(.appAccent)
            }
            .padding(theme.spacing.medium)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    private var themedDivider: some View {
        Rectangle()
            .fill(.appOutline)
            .frame(height: 1)
    }
}

#Preview {
    AboutSheet(
        appName: "TvManiac",
        versionText: "Version 1.0.0",
        aboutTitle: "About",
        aboutDescription: "TvManiac is a TV show tracking app built with Kotlin Multiplatform and SwiftUI.",
        sourceCodeLabel: "Source Code",
        sourceCodeAction: "GitHub",
        apiDisclaimer: "This product uses the TMDB API but is not endorsed or certified by TMDB.",
        icon: Image(systemName: "tv.fill"),
        onVersionTap: {},
        onSourceCodeTap: {}
    )
    .appPreview()
}
