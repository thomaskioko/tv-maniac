import SwiftUI

/// A circular button with a glass/blur effect matching the toolbar design.
/// Use `init(icon:action:)` for SF Symbol icons or `init(action:label:)` for custom content.
public struct GlassButton<Label: View>: View {
    @Environment(\.colorScheme) private var colorScheme

    private let action: () -> Void
    private let label: Label

    public init(action: @escaping () -> Void, @ViewBuilder label: () -> Label) {
        self.action = action
        self.label = label()
    }

    public var body: some View {
        Button(action: action) {
            ZStack {
                Circle()
                    .fill(Color.black.opacity(colorScheme == .dark ? 0.5 : 0.3))
                    .frame(width: 44, height: 44)
                    .overlay(
                        Circle()
                            .strokeBorder(Color.white.opacity(0.15), lineWidth: 1)
                    )
                    .shadow(color: Color.black.opacity(0.2), radius: 4, x: 0, y: 2)

                label
            }
        }
        .frame(width: 44, height: 44)
    }
}

public struct GlassButtonIconLabel: View {
    private let icon: String

    public init(icon: String) {
        self.icon = icon
    }

    public var body: some View {
        Image(systemName: icon)
            .font(.system(size: 18, weight: .semibold))
            .foregroundColor(.white)
    }
}

public extension GlassButton where Label == GlassButtonIconLabel {
    init(icon: String, action: @escaping () -> Void) {
        self.init(action: action) {
            GlassButtonIconLabel(icon: icon)
        }
    }
}

// MARK: - Preview

#Preview("Glass Button Styles") {
    ZStack {
        Color.black.ignoresSafeArea()

        VStack(spacing: 30) {
            HStack(spacing: 20) {
                GlassButton(icon: "chevron.left") {}
                GlassButton(icon: "ellipsis") {}
                GlassButton(icon: "magnifyingglass") {}
                GlassButton(icon: "gear") {}
            }

            HStack(spacing: 20) {
                GlassButton(icon: "heart") {}
                GlassButton(icon: "share") {}
                GlassButton(icon: "bookmark") {}
                GlassButton(icon: "play.fill") {}
            }
        }
    }
    .preferredColorScheme(.dark)
}
