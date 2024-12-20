import SwiftUI

public struct GlassToolbar: View {
  private let title: String
  private let opacity: Double
  @Environment(\.colorScheme) private var colorScheme

  public init(title: String, opacity: Double) {
    self.title = title
    self.opacity = opacity
  }

  public var body: some View {
    ZStack(alignment: .bottom) {
      VisualEffectView(effect: UIBlurEffect(style: colorScheme == .dark ? .dark : .light))
        .frame(height: 44 + ((UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first?.safeAreaInsets.top ?? 0))
        .opacity(opacity)
        .ignoresSafeArea()

      Text(title)
        .font(.system(size: 18, weight: .bold))
        .foregroundColor(colorScheme == .dark ? .white : .black)
        .opacity(opacity)
        .padding(.bottom, 16)
    }
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

  public init(backgroundColor: UIColor) {
    let appearance = UINavigationBarAppearance()
    appearance.configureWithTransparentBackground()
    appearance.backgroundColor = backgroundColor

    UINavigationBar.appearance().standardAppearance = appearance
    UINavigationBar.appearance().compactAppearance = appearance
    UINavigationBar.appearance().scrollEdgeAppearance = appearance

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
