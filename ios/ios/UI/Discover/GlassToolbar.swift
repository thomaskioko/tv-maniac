import SwiftUI


public struct GlassToolbar: View {
  private let title: String
  private let opacity: Double

  public init(title: String, opacity: Double) {
    self.title = title
    self.opacity = opacity
  }

  public var body: some View {
    ZStack(alignment: .bottom) {
      // Blur effect background
      VisualEffectView(effect: UIBlurEffect(style: .dark))
        .frame(height: 44 + ((UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first?.safeAreaInsets.top ?? 0))
        .opacity(opacity)
        .ignoresSafeArea()

      // Title
      Text(title)
        .font(.system(size: 18, weight: .bold))
        .foregroundColor(.white)
        .opacity(opacity)
        .padding(.bottom, 16)
    }
  }
}

// Add UIViewRepresentable for UIVisualEffectView
struct VisualEffectView: UIViewRepresentable {
  let effect: UIVisualEffect

  func makeUIView(context: UIViewRepresentableContext<Self>) -> UIVisualEffectView {
    UIVisualEffectView(effect: effect)
  }

  func updateUIView(_ uiView: UIVisualEffectView, context: UIViewRepresentableContext<Self>) {
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
extension View {
  func navigationBarColor(backgroundColor: UIColor) -> some View {
    self.modifier(NavigationBarModifier(backgroundColor: backgroundColor))
  }
}
