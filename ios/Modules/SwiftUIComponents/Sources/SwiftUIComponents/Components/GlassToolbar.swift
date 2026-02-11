import SwiftUI

public struct GlassToolbar<LeadingIcon: View, TrailingIcon: View>: View {
    @Theme private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let title: String
    private let opacity: Double
    private let isLoading: Bool
    private let leadingIcon: (() -> LeadingIcon)?
    private let trailingIcon: (() -> TrailingIcon)?

    public init(
        title: String,
        opacity: Double,
        isLoading: Bool = false,
        @ViewBuilder leadingIcon: @escaping () -> LeadingIcon,
        @ViewBuilder trailingIcon: @escaping () -> TrailingIcon
    ) {
        self.title = title
        self.opacity = opacity
        self.isLoading = isLoading
        self.leadingIcon = leadingIcon
        self.trailingIcon = trailingIcon
    }

    public init(
        title: String,
        opacity: Double,
        isLoading: Bool = false,
        @ViewBuilder trailingIcon: @escaping () -> TrailingIcon
    ) where LeadingIcon == EmptyView {
        self.title = title
        self.opacity = opacity
        self.isLoading = isLoading
        leadingIcon = nil
        self.trailingIcon = trailingIcon
    }

    public init(
        title: String,
        opacity: Double,
        isLoading: Bool = false,
        @ViewBuilder leadingIcon: @escaping () -> LeadingIcon
    ) where TrailingIcon == EmptyView {
        self.title = title
        self.opacity = opacity
        self.isLoading = isLoading
        self.leadingIcon = leadingIcon
        trailingIcon = nil
    }

    public init(
        title: String,
        opacity: Double,
        isLoading: Bool = false
    ) where LeadingIcon == EmptyView, TrailingIcon == EmptyView {
        self.title = title
        self.opacity = opacity
        self.isLoading = isLoading
        leadingIcon = nil
        trailingIcon = nil
    }

    public var body: some View {
        let toolbarHeight: CGFloat = 44
        let topPadding = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first?.safeAreaInsets
            .top ?? 0
        let blurStyle: UIBlurEffect.Style = colorScheme == .dark ? .systemThinMaterialDark : .systemThinMaterialLight

        ZStack(alignment: .top) {
            theme.colors.surface
                .frame(height: toolbarHeight + topPadding)
                .opacity(opacity)
                .ignoresSafeArea()
                .allowsHitTesting(false)

            VisualEffectView(effect: UIBlurEffect(style: blurStyle))
                .frame(height: toolbarHeight + topPadding)
                .opacity(opacity * 0.8)
                .ignoresSafeArea()
                .allowsHitTesting(false)

            HStack(spacing: theme.spacing.medium) {
                if let leadingIcon {
                    leadingIcon()
                } else {
                    Rectangle()
                        .fill(Color.clear)
                        .frame(width: 44)
                }

                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundColor(theme.colors.onSurface)
                    .opacity(opacity)
                    .lineLimit(1)
                    .padding(.bottom, theme.spacing.xSmall)
                    .frame(maxWidth: .infinity, alignment: .center)

                if isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.onSurface))
                        .scaleEffect(0.8)
                        .frame(width: 44)
                } else if let trailingIcon {
                    trailingIcon()
                } else {
                    Rectangle()
                        .fill(Color.clear)
                        .frame(width: 44)
                }
            }
            .padding(.horizontal, theme.spacing.medium)
            .frame(height: toolbarHeight)
            .padding(.top, topPadding)
        }
        .frame(maxWidth: .infinity)
    }
}

struct VisualEffectView: UIViewRepresentable {
    let effect: UIVisualEffect

    func makeUIView(context _: UIViewRepresentableContext<Self>) -> UIVisualEffectView {
        UIVisualEffectView(effect: effect)
    }

    func updateUIView(_ uiView: UIVisualEffectView, context _: UIViewRepresentableContext<Self>) {
        uiView.effect = effect
    }
}

public struct NavigationBarModifier: ViewModifier {
    private var backgroundColor: UIColor
    @Environment(\.colorScheme) private var colorScheme

    public init(backgroundColor: UIColor) {
        let appearance = UINavigationBarAppearance()
        appearance.configureWithTransparentBackground()
        appearance.backgroundColor = backgroundColor

        appearance.backButtonAppearance.normal.titleTextAttributes = [.foregroundColor: UIColor.clear]

        let backImage = UIImage(systemName: "chevron.left")?
            .withTintColor(.tintColor, renderingMode: .alwaysOriginal)
        appearance.setBackIndicatorImage(backImage, transitionMaskImage: backImage)

        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().compactAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
        UINavigationBar.appearance().tintColor = .tintColor

        self.backgroundColor = backgroundColor
    }

    public func body(content: Content) -> some View {
        content
    }
}

// Add this extension to make navigation bar transparent
public extension View {
    func navigationBarColor(backgroundColor: UIColor) -> some View {
        modifier(NavigationBarModifier(backgroundColor: backgroundColor))
    }
}

// MARK: - Glass Button Component

/// A circular button with a glass/blur effect matching the toolbar design
public struct GlassButton: View {
    @Environment(\.colorScheme) private var colorScheme

    private let icon: String
    private let action: () -> Void

    public init(icon: String, action: @escaping () -> Void) {
        self.icon = icon
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            ZStack {
                // Semi-transparent background circle
                Circle()
                    .fill(Color.black.opacity(colorScheme == .dark ? 0.5 : 0.3))
                    .frame(width: 44, height: 44)
                    .overlay(
                        Circle()
                            .strokeBorder(Color.white.opacity(0.15), lineWidth: 1)
                    )
                    .shadow(color: Color.black.opacity(0.2), radius: 4, x: 0, y: 2)

                // Icon
                Image(systemName: icon)
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(.white)
            }
        }
        .frame(width: 44, height: 44)
    }
}

// MARK: - Preview

#Preview("Glass Toolbar with Buttons") {
    ZStack {
        // Background image simulation
        LinearGradient(
            gradient: Gradient(colors: [.white]),
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        )
        .ignoresSafeArea()

        VStack {
            GlassToolbar(
                title: "Detective",
                opacity: 1.0,
                isLoading: false,
                leadingIcon: {
                    GlassButton(icon: "chevron.left") {
                        print("Back tapped")
                    }
                },
                trailingIcon: {
                    GlassButton(icon: "ellipsis") {
                        print("Menu tapped")
                    }
                }
            )

            Spacer()
        }
    }
    .preferredColorScheme(.dark)
}

#Preview("Glass Toolbar - Loading State") {
    ZStack {
        VStack {
            GlassToolbar(
                title: "Loading...",
                opacity: 1.0,
                isLoading: true,
                leadingIcon: {
                    GlassButton(icon: "chevron.left") {
                        print("Back tapped")
                    }
                }
            )

            Spacer()
        }
    }
    .preferredColorScheme(.dark)
}

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
