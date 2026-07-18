import DesignSystem
import SwiftUI

public struct FilledVerticalIconButton: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.hapticFeedbackEnabled) private var hapticFeedbackEnabled

    private let text: String
    private let systemImage: String
    private let containerColor: Color?
    private let symbolEffectValue: AnyHashable?
    private let symbolEffectDirection: SymbolEffectDirection
    private let action: () -> Void

    public enum SymbolEffectDirection {
        case up
        case down
        case none
    }

    public init(
        text: String,
        systemImage: String,
        containerColor: Color? = nil,
        symbolEffectValue: AnyHashable? = nil,
        symbolEffectDirection: SymbolEffectDirection = .none,
        action: @escaping () -> Void
    ) {
        self.text = text
        self.systemImage = systemImage
        self.containerColor = containerColor
        self.symbolEffectValue = symbolEffectValue
        self.symbolEffectDirection = symbolEffectDirection
        self.action = action
    }

    public var body: some View {
        Button(action: {
            Haptics.impact(isEnabled: hapticFeedbackEnabled)
            action()
        }) {
            VStack(spacing: theme.spacing.xxxSmall) {
                iconImage

                Text(text)
                    .lineLimit(1)
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(.appOnButtonBackground)
            }
            .padding(.vertical, theme.spacing.xxSmall)
            .frame(width: Constants.minWidth, height: Constants.minHeight)
        }
        .buttonStyle(.borderedProminent)
        .controlSize(.small)
        .tint(containerColor ?? theme.colors.buttonBackground)
        .buttonBorderShape(.roundedRectangle(radius: theme.shapes.medium))
    }

    @ViewBuilder
    private var iconImage: some View {
        switch symbolEffectDirection {
        case .up:
            Image(systemName: systemImage)
                .foregroundStyle(.appOnButtonBackground)
                .symbolEffect(.bounce.up, value: symbolEffectValue)
        case .down:
            Image(systemName: systemImage)
                .foregroundStyle(.appOnButtonBackground)
                .symbolEffect(.bounce.down, value: symbolEffectValue)
        case .none:
            Image(systemName: systemImage)
                .foregroundStyle(.appOnButtonBackground)
        }
    }

    private enum Constants {
        static let minWidth: CGFloat = 85
        static let minHeight: CGFloat = 35
    }
}

#Preview {
    HStack(spacing: TvManiacSpacingScheme.default.small) {
        FilledVerticalIconButton(
            text: "Track",
            systemImage: "plus.circle.fill",
            action: {}
        )

        FilledVerticalIconButton(
            text: "Stop",
            systemImage: "minus.circle.fill",
            containerColor: .red.opacity(0.65),
            action: {}
        )
    }
}
