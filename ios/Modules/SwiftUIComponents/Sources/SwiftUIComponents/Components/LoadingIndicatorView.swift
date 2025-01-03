import SwiftUI

public struct LoadingIndicatorView: View {
  private let style: StrokeStyle
  @State private var animate: Bool

  public init(
    style: StrokeStyle = StrokeStyle(lineWidth: 3, lineCap: .round),
    animate: Bool = true
  ) {
    self.style = style
    self.animate = animate
  }

  public var body: some View {
    ZStack {
      Spacer()

      Circle()
        .trim(from: 0, to: 0.2)
        .stroke(
          AngularGradient(
            gradient: .init(colors: [Color.accent]),
            center: .center
          ),
          style: style
        )
        .rotationEffect(Angle(degrees: animate ? 360 : 0))
        .animation(.linear(duration: 0.7).repeatForever(autoreverses: false), value: animate)
        .frame(width: 100, height: 50)

      Circle()
        .trim(from: 0.5, to: 0.7)
        .stroke(
          AngularGradient(
            gradient: .init(colors: [Color.accent]),
            center: .center
          ),
          style: style
        )
        .rotationEffect(Angle(degrees: animate ? 360 : 0))
        .animation(.linear(duration: 0.7).repeatForever(autoreverses: false), value: animate)
        .frame(width: 100, height: 50)

      Spacer()
    }
    .padding(16)
    .edgesIgnoringSafeArea(.all)
    .onAppear {
      self.animate.toggle()
    }
  }
}

#Preview {
  LoadingIndicatorView()
}
