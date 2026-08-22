import Components
import DesignSystem
import SwiftUI

public struct WatchDateSelectionContent: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let currentWatchedAtLabel: String?
    private let justNowLabel: String
    private let releaseDateLabel: String
    private let otherDateLabel: String
    private let unknownDateLabel: String
    private let confirmLabel: String
    private let cancelLabel: String
    private let isReleaseDateEnabled: Bool
    private let onJustNow: () -> Void
    private let onReleaseDate: () -> Void
    private let onOtherDate: (Date) -> Void
    private let onUnknownDate: () -> Void

    @State private var isPickingDate = false
    @State private var chosenDate = Date.now
    @State private var latestSelectableDate = Date.now

    public init(
        title: String,
        currentWatchedAtLabel: String? = nil,
        justNowLabel: String,
        releaseDateLabel: String,
        otherDateLabel: String,
        unknownDateLabel: String,
        confirmLabel: String,
        cancelLabel: String,
        isReleaseDateEnabled: Bool,
        onJustNow: @escaping () -> Void,
        onReleaseDate: @escaping () -> Void,
        onOtherDate: @escaping (Date) -> Void,
        onUnknownDate: @escaping () -> Void
    ) {
        self.title = title
        self.currentWatchedAtLabel = currentWatchedAtLabel
        self.justNowLabel = justNowLabel
        self.releaseDateLabel = releaseDateLabel
        self.otherDateLabel = otherDateLabel
        self.unknownDateLabel = unknownDateLabel
        self.confirmLabel = confirmLabel
        self.cancelLabel = cancelLabel
        self.isReleaseDateEnabled = isReleaseDateEnabled
        self.onJustNow = onJustNow
        self.onReleaseDate = onReleaseDate
        self.onOtherDate = onOtherDate
        self.onUnknownDate = onUnknownDate
    }

    public var body: some View {
        VStack(spacing: 0) {
            grabber

            header

            if isPickingDate {
                datePicker
            } else {
                actions
            }
        }
        .padding(.bottom, theme.spacing.large)
        .frame(maxWidth: .infinity)
        .background(.appSurface)
        .clipShape(.rect(topLeadingRadius: sheetCornerRadius, topTrailingRadius: sheetCornerRadius))
        .onAppear { latestSelectableDate = .now }
    }

    private var grabber: some View {
        RoundedRectangle(cornerRadius: 2.5)
            .fill(theme.colors.onSurface.opacity(0.4))
            .frame(width: 36, height: 5)
            .frame(maxWidth: .infinity, alignment: .center)
            .padding(.vertical, theme.spacing.small)
    }

    private var header: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            Text(title)
                .textStyle(theme.typography.titleLarge)
                .foregroundStyle(.appOnSurface)

            if let currentWatchedAtLabel {
                Text(currentWatchedAtLabel)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(theme.colors.onSurfaceVariant)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, theme.spacing.medium)
        .padding(.bottom, theme.spacing.small)
    }

    private var actions: some View {
        VStack(spacing: 0) {
            SheetActionItem(
                icon: "checkmark",
                label: justNowLabel,
                action: onJustNow
            )

            SheetActionItem(
                icon: "calendar.badge.clock",
                label: releaseDateLabel,
                isEnabled: isReleaseDateEnabled,
                action: onReleaseDate
            )

            SheetActionItem(
                icon: "calendar",
                label: otherDateLabel,
                action: {
                    chosenDate = min(.now, latestSelectableDate)
                    isPickingDate = true
                }
            )

            SheetActionItem(
                icon: "questionmark",
                label: unknownDateLabel,
                action: onUnknownDate
            )
        }
    }

    private var datePicker: some View {
        VStack(spacing: theme.spacing.medium) {
            DatePicker(
                "",
                selection: $chosenDate,
                in: ...latestSelectableDate,
                displayedComponents: [.date, .hourAndMinute]
            )
            .datePickerStyle(.graphical)
            .labelsHidden()

            HStack(spacing: theme.spacing.medium) {
                Button(cancelLabel) { isPickingDate = false }
                    .buttonStyle(.bordered)

                Button(confirmLabel) { onOtherDate(chosenDate) }
                    .buttonStyle(.borderedProminent)
            }
            .frame(maxWidth: .infinity, alignment: .trailing)
        }
        .padding(.horizontal, theme.spacing.medium)
    }
}

private let sheetCornerRadius: CGFloat = 16

#Preview("Default") {
    WatchDateSelectionContent(
        title: "When did you watch this?",
        justNowLabel: "Just now",
        releaseDateLabel: "Release date",
        otherDateLabel: "Other date…",
        unknownDateLabel: "Unknown date",
        confirmLabel: "OK",
        cancelLabel: "Cancel",
        isReleaseDateEnabled: true,
        onJustNow: {},
        onReleaseDate: {},
        onOtherDate: { _ in },
        onUnknownDate: {}
    )
}

#Preview("Correcting a mark") {
    WatchDateSelectionContent(
        title: "Change watched date",
        currentWatchedAtLabel: "12 Jan 2026 20:30",
        justNowLabel: "Just now",
        releaseDateLabel: "Release date",
        otherDateLabel: "Other date…",
        unknownDateLabel: "Unknown date",
        confirmLabel: "OK",
        cancelLabel: "Cancel",
        isReleaseDateEnabled: false,
        onJustNow: {},
        onReleaseDate: {},
        onOtherDate: { _ in },
        onUnknownDate: {}
    )
}
