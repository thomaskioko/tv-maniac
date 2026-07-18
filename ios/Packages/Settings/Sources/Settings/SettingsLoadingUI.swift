import Components
import DesignSystem
import SwiftUI

struct SettingsLoadingUI: View {
    @Environment(\.appTheme) private var appTheme

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            ForEach([3, 4, 2], id: \.self) { rowCount in
                VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                    ShimmerView()
                        .frame(width: 96, height: 14)
                        .padding(.leading, appTheme.spacing.small)
                    card {
                        ForEach(0 ..< rowCount, id: \.self) { index in
                            row
                            if index != rowCount - 1 {
                                divider
                            }
                        }
                    }
                }
            }
        }
    }

    private var row: some View {
        HStack(spacing: appTheme.spacing.medium) {
            ShimmerView(cornerRadius: appTheme.shapes.medium)
                .frame(width: 36, height: 36)

            VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                ShimmerView()
                    .frame(width: 150, height: 16)
                ShimmerView()
                    .frame(width: 210, height: 12)
            }

            Spacer()
        }
        .padding(.horizontal, appTheme.spacing.medium)
        .padding(.vertical, appTheme.spacing.small)
    }

    private func card(@ViewBuilder content: () -> some View) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(appTheme.colors.surface)
        .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.large))
        .overlay(
            RoundedRectangle(cornerRadius: appTheme.shapes.large)
                .stroke(appTheme.colors.outline.opacity(0.2), lineWidth: 0.5)
        )
    }

    private var divider: some View {
        Rectangle()
            .fill(appTheme.colors.outline.opacity(0.2))
            .frame(height: 0.5)
            .padding(.leading, appTheme.spacing.xxxLarge)
    }
}

#Preview("Settings Loading") {
    SettingsLoadingUI()
        .padding(.horizontal, TvManiacSpacingScheme.default.medium)
        .appPreview()
}
