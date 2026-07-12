import Components
import DesignSystem
import SwiftUI

struct SettingsCard<Content: View>: View {
    @Environment(\.appTheme) private var appTheme
    private let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            content
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(appTheme.colors.surface)
        .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.large))
        .overlay(
            RoundedRectangle(cornerRadius: appTheme.shapes.large)
                .stroke(appTheme.colors.outline.opacity(0.2), lineWidth: 0.5)
        )
    }
}

struct SettingsSectionLabel: View {
    @Environment(\.appTheme) private var appTheme
    private let title: String

    init(_ title: String) {
        self.title = title
    }

    var body: some View {
        Text(title)
            .textStyle(appTheme.typography.labelMedium)
            .foregroundColor(appTheme.colors.onSurfaceVariant)
            .padding(.leading, appTheme.spacing.small)
    }
}

struct SettingsRowDivider: View {
    @Environment(\.appTheme) private var appTheme

    var body: some View {
        Rectangle()
            .fill(appTheme.colors.outline.opacity(0.2))
            .frame(height: 0.5)
            .padding(.leading, 64)
    }
}

struct SettingsIconChip: View {
    @Environment(\.appTheme) private var appTheme
    private let systemName: String

    init(_ systemName: String) {
        self.systemName = systemName
    }

    var body: some View {
        RoundedRectangle(cornerRadius: appTheme.shapes.medium)
            .fill(appTheme.colors.secondary.opacity(0.12))
            .frame(width: 36, height: 36)
            .overlay(
                Image(systemName: systemName)
                    .foregroundColor(appTheme.colors.secondary)
            )
    }
}

struct SettingsNavigationRow: View {
    @Environment(\.appTheme) private var appTheme
    private let item: SettingsNavigationItem

    init(_ item: SettingsNavigationItem) {
        self.item = item
    }

    var body: some View {
        Button(action: item.onTap) {
            HStack(spacing: appTheme.spacing.medium) {
                SettingsIconChip(item.icon)

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(appTheme.typography.bodyLarge)
                        .foregroundColor(appTheme.colors.onSurface)
                    if let subtitle = item.subtitle {
                        Text(subtitle)
                            .textStyle(appTheme.typography.bodySmall)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                    }
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.vertical, appTheme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

struct SettingsToggleRow: View {
    @Environment(\.appTheme) private var appTheme
    @Environment(\.hapticFeedbackEnabled) private var hapticFeedbackEnabled
    private let item: SettingsToggleItem

    init(_ item: SettingsToggleItem) {
        self.item = item
    }

    var body: some View {
        HStack(spacing: appTheme.spacing.medium) {
            SettingsIconChip(item.icon)

            VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                Text(item.title)
                    .textStyle(appTheme.typography.bodyLarge)
                    .foregroundColor(appTheme.colors.onSurface)
                    .lineLimit(1)
                if item.isLocked {
                    LockBadge(
                        text: item.lockedBadgeText,
                        accessibilityLabel: item.lockedAccessibilityLabel
                    )
                }
                Text(item.subtitle)
                    .textStyle(appTheme.typography.bodySmall)
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
                if let secondarySubtitle = item.secondarySubtitle {
                    Text(secondarySubtitle)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }

            Spacer()

            Toggle("", isOn: Binding(
                get: { item.isOn },
                set: {
                    Haptics.impact(isEnabled: hapticFeedbackEnabled)
                    item.onToggle($0)
                }
            ))
            .labelsHidden()
            .tint(appTheme.colors.secondary)
            .disabled(item.isLocked)
        }
        .padding(.horizontal, appTheme.spacing.medium)
        .padding(.vertical, appTheme.spacing.small)
    }
}

struct SettingsFontSizeRow: View {
    @Environment(\.appTheme) private var appTheme
    @Environment(\.hapticFeedbackEnabled) private var hapticFeedbackEnabled
    private let item: SettingsFontSizeItem
    @State private var sliderPercent: Double

    private static let range: ClosedRange<Double> = 85 ... 130
    private static let step: Double = 5
    private static let defaultPercent = 100

    init(_ item: SettingsFontSizeItem) {
        self.item = item
        _sliderPercent = State(initialValue: Double(item.percent))
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.small) {
            HStack(alignment: .top, spacing: appTheme.spacing.small) {
                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(appTheme.typography.bodyLarge)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(item.description)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }

                Spacer()

                if Int(sliderPercent) != Self.defaultPercent {
                    Button(action: resetToDefault) {
                        Text(item.resetLabel)
                            .textStyle(appTheme.typography.labelLarge)
                            .foregroundColor(appTheme.colors.secondary)
                    }
                }

                Text("\(Int(sliderPercent))%")
                    .textStyle(appTheme.typography.labelLarge)
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
                    .monospacedDigit()
            }

            Text(item.previewText)
                .textStyle(appTheme.typography.bodyMedium)
                .foregroundColor(appTheme.colors.onSurface)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(appTheme.spacing.small)
                .background(appTheme.colors.background)
                .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.medium))

            SteppedSlider(
                value: $sliderPercent,
                range: Self.range,
                step: Self.step,
                onEditingChanged: { isEditing in
                    guard !isEditing else { return }
                    Haptics.impact(isEnabled: hapticFeedbackEnabled)
                    item.onPercentChange(Int(sliderPercent))
                }
            )
            .accessibilityLabel(item.title)
        }
        .padding(appTheme.spacing.medium)
    }

    private func resetToDefault() {
        Haptics.impact(isEnabled: hapticFeedbackEnabled)
        sliderPercent = Double(Self.defaultPercent)
        item.onPercentChange(Self.defaultPercent)
    }
}

private struct SteppedSlider: View {
    @Environment(\.appTheme) private var appTheme
    @Binding var value: Double
    let range: ClosedRange<Double>
    let step: Double
    let onEditingChanged: (Bool) -> Void

    private let trackHeight: CGFloat = 16
    private let thumbWidth: CGFloat = 4
    private let thumbHeight: CGFloat = 28
    private let thumbGap: CGFloat = 6
    private let tickSize: CGFloat = 4

    private var stops: [Double] {
        stride(from: range.lowerBound, through: range.upperBound, by: step).map { $0 }
    }

    var body: some View {
        GeometryReader { geo in
            let width = geo.size.width
            let fraction = CGFloat((value - range.lowerBound) / (range.upperBound - range.lowerBound))

            ZStack(alignment: .leading) {
                Capsule()
                    .fill(appTheme.colors.surfaceVariant)
                    .frame(width: width, height: trackHeight)

                Capsule()
                    .fill(appTheme.colors.secondary)
                    .frame(width: max(width * fraction - thumbGap, 0), height: trackHeight)

                ForEach(Array(stops.enumerated()), id: \.offset) { _, stop in
                    let tickFraction = CGFloat((stop - range.lowerBound) / (range.upperBound - range.lowerBound))
                    Circle()
                        .fill(stop <= value ? appTheme.colors.onSecondary : appTheme.colors.onSurfaceVariant)
                        .frame(width: tickSize, height: tickSize)
                        .offset(x: (width - tickSize) * tickFraction)
                }

                Capsule()
                    .fill(appTheme.colors.secondary)
                    .frame(width: thumbWidth, height: thumbHeight)
                    .shadow(color: .black.opacity(0.2), radius: 2, y: 1)
                    .offset(x: width * fraction - thumbWidth / 2)
            }
            .frame(height: thumbHeight)
            .contentShape(Rectangle())
            .gesture(
                DragGesture(minimumDistance: 0)
                    .onChanged { gesture in
                        onEditingChanged(true)
                        updateValue(at: gesture.location.x, width: width)
                    }
                    .onEnded { _ in onEditingChanged(false) }
            )
        }
        .frame(height: thumbHeight)
        .accessibilityElement()
        .accessibilityValue("\(Int(value))%")
        .accessibilityAdjustableAction { direction in
            switch direction {
            case .increment: value = min(value + step, range.upperBound)
            case .decrement: value = max(value - step, range.lowerBound)
            default: break
            }
            onEditingChanged(false)
        }
    }

    private func updateValue(at x: CGFloat, width: CGFloat) {
        let clampedFraction = min(max(x / width, 0), 1)
        let raw = range.lowerBound + Double(clampedFraction) * (range.upperBound - range.lowerBound)
        let snapped = (raw / step).rounded() * step
        value = min(max(snapped, range.lowerBound), range.upperBound)
    }
}

struct SettingsLinkRow: View {
    @Environment(\.appTheme) private var appTheme
    private let item: SettingsLinkItem

    init(_ item: SettingsLinkItem) {
        self.item = item
    }

    var body: some View {
        Button(action: item.onOpen) {
            HStack(alignment: .top, spacing: appTheme.spacing.medium) {
                if let asset = item.leadingAsset {
                    Image(asset, bundle: .module)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 36, height: 36)
                } else if let leading = item.leadingSystemImage {
                    Image(systemName: leading)
                        .foregroundColor(appTheme.colors.secondary)
                        .frame(width: appTheme.spacing.large, height: appTheme.spacing.large)
                }

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(appTheme.typography.titleMedium)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(item.body)
                        .textStyle(appTheme.typography.bodyMedium)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                    Text(item.link)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.secondary)
                        .lineLimit(1)
                }

                Spacer()

                Image(systemName: "arrow.up.right.square")
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.vertical, appTheme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
