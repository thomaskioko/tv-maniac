import DesignSystem
import Models
import SwiftUI

public struct ProviderSignInCard: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let description: String
    private let providers: [SwiftAuthProvider]
    private let showBackground: Bool
    private let onProviderSelected: (String) -> Void

    public init(
        title: String,
        description: String,
        providers: [SwiftAuthProvider],
        showBackground: Bool = true,
        onProviderSelected: @escaping (String) -> Void
    ) {
        self.title = title
        self.description = description
        self.providers = providers
        self.showBackground = showBackground
        self.onProviderSelected = onProviderSelected
    }

    public var body: some View {
        if showBackground {
            content
                .padding(theme.spacing.medium)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(theme.colors.surface)
                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large))
                .overlay(
                    RoundedRectangle(cornerRadius: theme.shapes.large)
                        .stroke(theme.colors.outline.opacity(0.2), lineWidth: 0.5)
                )
        } else {
            content
        }
    }

    private var content: some View {
        VStack(alignment: .leading, spacing: theme.spacing.large) {
            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                Text(title)
                    .textStyle(theme.typography.titleLarge)
                    .foregroundColor(theme.colors.onSurface)
                Text(description)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }

            VStack(spacing: theme.spacing.medium) {
                ForEach(providers) { provider in
                    ProviderButton(title: provider.label, logo: provider.logoName) {
                        onProviderSelected(provider.id)
                    }
                }
            }
        }
    }
}

#if DEBUG
    private let previewProviders: [SwiftAuthProvider] = [
        SwiftAuthProvider(id: "TRAKT", label: "Continue with Trakt", logoName: "TraktMono"),
        SwiftAuthProvider(id: "SIMKL", label: "Continue with Simkl", logoName: "SimklMono"),
    ]

    #Preview("With background") {
        ProviderSignInCard(
            title: "Connect & Sync Your Content",
            description: "Save your progress, discover new titles, and sync your content across all devices.",
            providers: previewProviders,
            onProviderSelected: { _ in }
        )
        .padding()
        .appPreview()
    }

    #Preview("No background") {
        ProviderSignInCard(
            title: "Connect & Sync Your Content",
            description: "Save your progress, discover new titles, and sync your content across all devices.",
            providers: previewProviders,
            showBackground: false,
            onProviderSelected: { _ in }
        )
        .padding()
        .appPreview()
    }
#endif
