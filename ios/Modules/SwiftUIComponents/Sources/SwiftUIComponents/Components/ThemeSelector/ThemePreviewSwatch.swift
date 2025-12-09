import SwiftUI

public struct ThemePreviewSwatch: View {
    let backgroundColor: Color
    let accentColor: Color
    let onAccentColor: Color
    let displayName: String
    let isSelected: Bool
    let isSystemTheme: Bool
    let onSelect: () -> Void

    public init(
        backgroundColor: Color,
        accentColor: Color,
        onAccentColor: Color,
        displayName: String,
        isSelected: Bool,
        isSystemTheme: Bool = false,
        onSelect: @escaping () -> Void
    ) {
        self.backgroundColor = backgroundColor
        self.accentColor = accentColor
        self.onAccentColor = onAccentColor
        self.displayName = displayName
        self.isSelected = isSelected
        self.isSystemTheme = isSystemTheme
        self.onSelect = onSelect
    }

    public var body: some View {
        Button(action: onSelect) {
            VStack(spacing: 4) {
                ZStack(alignment: .bottomTrailing) {
                    if isSystemTheme {
                        SystemThemeSwatch(isSelected: isSelected)
                            .frame(width: 56, height: 56)
                    } else {
                        Circle()
                            .fill(backgroundColor)
                            .frame(width: 56, height: 56)
                            .overlay(
                                Circle()
                                    .fill(accentColor)
                                    .frame(width: 24, height: 24)
                            )
                            .overlay(
                                Circle()
                                    .stroke(
                                        isSelected ? accentColor : Color(UIColor.separator),
                                        lineWidth: isSelected ? 3 : 1
                                    )
                            )
                    }

                    if isSelected {
                        Circle()
                            .fill(isSystemTheme ? Color.accentColor : accentColor)
                            .frame(width: 20, height: 20)
                            .overlay(
                                Image(systemName: "checkmark")
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(isSystemTheme ? .white : onAccentColor)
                            )
                            .offset(x: 4, y: 4)
                    }
                }
                .frame(width: 64, height: 64)

                Text(displayName)
                    .font(.caption2)
                    .lineLimit(1)
                    .foregroundColor(isSelected ? .secondary : .primary)
            }
            .padding(8)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

struct SystemThemeSwatch: View {
    let isSelected: Bool

    var body: some View {
        Canvas { context, size in
            let lightPath = Path { path in
                path.move(to: CGPoint(x: 0, y: 0))
                path.addLine(to: CGPoint(x: size.width, y: 0))
                path.addLine(to: CGPoint(x: 0, y: size.height))
                path.closeSubpath()
            }

            let darkPath = Path { path in
                path.move(to: CGPoint(x: size.width, y: 0))
                path.addLine(to: CGPoint(x: size.width, y: size.height))
                path.addLine(to: CGPoint(x: 0, y: size.height))
                path.closeSubpath()
            }

            context.fill(lightPath, with: .color(TvManiacColorScheme.light.background))
            context.fill(darkPath, with: .color(TvManiacColorScheme.dark.background))

            let lightAccentCenter = CGPoint(x: size.width * 0.35, y: size.height * 0.35)
            let darkAccentCenter = CGPoint(x: size.width * 0.65, y: size.height * 0.65)
            let accentRadius = size.width * 0.15

            context.fill(
                Circle().path(in: CGRect(
                    x: lightAccentCenter.x - accentRadius,
                    y: lightAccentCenter.y - accentRadius,
                    width: accentRadius * 2,
                    height: accentRadius * 2
                )),
                with: .color(TvManiacColorScheme.light.secondary)
            )

            context.fill(
                Circle().path(in: CGRect(
                    x: darkAccentCenter.x - accentRadius,
                    y: darkAccentCenter.y - accentRadius,
                    width: accentRadius * 2,
                    height: accentRadius * 2
                )),
                with: .color(TvManiacColorScheme.dark.secondary)
            )
        }
        .clipShape(Circle())
        .overlay(
            Circle()
                .stroke(
                    isSelected ? Color.accentColor : Color(UIColor.separator),
                    lineWidth: isSelected ? 3 : 1
                )
        )
    }
}

#Preview {
    VStack(spacing: 20) {
        HStack(spacing: 16) {
            ThemePreviewSwatch(
                backgroundColor: TvManiacColorScheme.light.background,
                accentColor: TvManiacColorScheme.light.secondary,
                onAccentColor: TvManiacColorScheme.light.onSecondary,
                displayName: "System",
                isSelected: true,
                isSystemTheme: true,
                onSelect: {}
            )

            ThemePreviewSwatch(
                backgroundColor: TvManiacColorScheme.light.background,
                accentColor: TvManiacColorScheme.light.secondary,
                onAccentColor: TvManiacColorScheme.light.onSecondary,
                displayName: "Light",
                isSelected: false,
                onSelect: {}
            )

            ThemePreviewSwatch(
                backgroundColor: TvManiacColorScheme.dark.background,
                accentColor: TvManiacColorScheme.dark.secondary,
                onAccentColor: TvManiacColorScheme.dark.onSecondary,
                displayName: "Dark",
                isSelected: true,
                onSelect: {}
            )
        }

        HStack(spacing: 16) {
            ThemePreviewSwatch(
                backgroundColor: TvManiacColorScheme.terminal.background,
                accentColor: TvManiacColorScheme.terminal.secondary,
                onAccentColor: TvManiacColorScheme.terminal.onSecondary,
                displayName: "Terminal",
                isSelected: false,
                onSelect: {}
            )

            ThemePreviewSwatch(
                backgroundColor: TvManiacColorScheme.autumn.background,
                accentColor: TvManiacColorScheme.autumn.secondary,
                onAccentColor: TvManiacColorScheme.autumn.onSecondary,
                displayName: "Autumn",
                isSelected: false,
                onSelect: {}
            )

            ThemePreviewSwatch(
                backgroundColor: TvManiacColorScheme.aqua.background,
                accentColor: TvManiacColorScheme.aqua.secondary,
                onAccentColor: TvManiacColorScheme.aqua.onSecondary,
                displayName: "Aqua",
                isSelected: true,
                onSelect: {}
            )
        }
    }
    .padding()
    .background(Color(.systemBackground))
}
