import SwiftUI

public struct PremiumOverlayModifier: ViewModifier {
    @Environment(\.appTheme) private var theme

    private let isLocked: Bool
    private let badgeText: String?
    private let title: String?
    private let message: String?
    private let actionText: String?
    private let onActionClick: (() -> Void)?
    private let accessibilityLabel: String?

    private static let blurRadius: CGFloat = 16
    private static let lineSpacing: CGFloat = 12
    private static let washOpacity: CGFloat = 0.67
    private static let cardOpacity: CGFloat = 0.65

    private var cardBadgeText: String? {
        guard let badgeText, !badgeText.isEmpty else { return nil }
        return badgeText
    }

    private var cardActionText: String? {
        guard let actionText, !actionText.isEmpty else { return nil }
        return actionText
    }

    public init(
        isLocked: Bool,
        badgeText: String? = nil,
        title: String? = nil,
        message: String? = nil,
        actionText: String? = nil,
        onActionClick: (() -> Void)? = nil,
        accessibilityLabel: String? = nil
    ) {
        self.isLocked = isLocked
        self.badgeText = badgeText
        self.title = title
        self.message = message
        self.actionText = actionText
        self.onActionClick = onActionClick
        self.accessibilityLabel = accessibilityLabel
    }

    public func body(content: Content) -> some View {
        content
            .blur(radius: isLocked ? Self.blurRadius : 0)
            .allowsHitTesting(!isLocked)
            .accessibilityElement(children: isLocked ? .ignore : .contain)
            .accessibilityLabel(isLocked ? (accessibilityLabel ?? "") : "")
            .overlay {
                if isLocked {
                    lockedCover
                }
            }
    }

    private var lockedCover: some View {
        Rectangle()
            .fill(theme.colors.background.opacity(Self.washOpacity))
            .overlay {
                Canvas { context, size in
                    var path = Path()
                    var startX: CGFloat = 0
                    while startX < size.width {
                        path.move(to: CGPoint(x: startX, y: 0))
                        path.addLine(to: CGPoint(x: size.width, y: size.width - startX))
                        startX += Self.lineSpacing
                    }
                    var startY = Self.lineSpacing
                    while startY < size.height {
                        path.move(to: CGPoint(x: 0, y: startY))
                        path.addLine(to: CGPoint(x: size.height - startY, y: size.height))
                        startY += Self.lineSpacing
                    }
                    context.stroke(path, with: .color(theme.colors.outline), lineWidth: 0.5)
                }
            }
            .overlay {
                if cardBadgeText != nil || title != nil {
                    lockedCard
                }
            }
    }

    private var lockedCard: some View {
        VStack(spacing: theme.spacing.xSmall) {
            if let cardBadgeText {
                badge(text: cardBadgeText)
            }

            if let title {
                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundStyle(theme.colors.onScrim)
                    .multilineTextAlignment(.center)
            }

            if let message {
                Text(message)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(theme.colors.onScrim.opacity(0.85))
                    .multilineTextAlignment(.center)
            }

            if let cardActionText, let onActionClick {
                Button(cardActionText, action: onActionClick)
                    .buttonStyle(PremiumUpgradeButtonStyle())
                    .padding(.top, theme.spacing.xxSmall)
            }
        }
        .padding(theme.spacing.medium)
        .background(
            RoundedRectangle(cornerRadius: theme.shapes.medium)
                .fill(theme.colors.scrim.opacity(Self.cardOpacity))
        )
        .padding(.horizontal, theme.spacing.xLarge)
    }

    private func badge(text: String) -> some View {
        HStack(spacing: theme.spacing.xxSmall) {
            Image(systemName: "lock.fill")
                .textStyle(theme.typography.labelSmall)
            Text(text)
                .textStyle(theme.typography.labelSmall)
                .lineLimit(1)
        }
        .fixedSize()
        .foregroundStyle(theme.colors.background)
        .padding(.horizontal, theme.spacing.xSmall)
        .padding(.vertical, theme.spacing.xxSmall)
        .background(
            RoundedRectangle(cornerRadius: theme.shapes.small)
                .fill(theme.colors.onSurface)
        )
    }
}

public extension View {
    func premiumOverlay(
        isLocked: Bool,
        badgeText: String? = nil,
        title: String? = nil,
        message: String? = nil,
        actionText: String? = nil,
        onActionClick: (() -> Void)? = nil,
        accessibilityLabel: String? = nil
    ) -> some View {
        modifier(PremiumOverlayModifier(
            isLocked: isLocked,
            badgeText: badgeText,
            title: title,
            message: message,
            actionText: actionText,
            onActionClick: onActionClick,
            accessibilityLabel: accessibilityLabel
        ))
    }
}

#Preview {
    VStack(spacing: 24) {
        Text("Custom Theme")
            .textStyle(TvManiacTypographyScheme.shared.titleMedium)
            .frame(width: 220, height: 120)
            .background(.appSurface)
            .premiumOverlay(isLocked: true, accessibilityLabel: "Locked")

        Text("Custom Theme")
            .textStyle(TvManiacTypographyScheme.shared.titleMedium)
            .frame(width: 220, height: 120)
            .background(.appSurface)
            .premiumOverlay(isLocked: false)
    }
    .padding()
    .background(.appBackground)
}

#Preview("With Card") {
    Text("Calendar content")
        .textStyle(TvManiacTypographyScheme.shared.bodyMedium)
        .frame(width: 320, height: 240)
        .background(.appSurface)
        .premiumOverlay(
            isLocked: true,
            badgeText: "Premium",
            title: "Calendar is a Premium feature",
            message: "Upgrade to see upcoming episodes for your shows",
            actionText: "Upgrade to Premium",
            onActionClick: {},
            accessibilityLabel: "Locked"
        )
        .padding()
        .background(.appBackground)
}

private struct PremiumUpgradeButtonStyle: ButtonStyle {
    @Environment(\.appTheme) private var theme

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .textStyle(theme.typography.labelLarge)
            .foregroundStyle(.appOnSecondary)
            .padding(.horizontal, theme.spacing.large)
            .padding(.vertical, theme.spacing.small)
            .background(
                .appSecondary,
                in: RoundedRectangle(cornerRadius: theme.shapes.medium)
            )
            .opacity(configuration.isPressed ? 0.8 : 1)
    }
}
