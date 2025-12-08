import SwiftUI

public struct LoadingIndicatorView: View {
    @Theme private var theme
    @State private var animate: Bool

    private let style: StrokeStyle

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
                        gradient: .init(colors: [theme.colors.accent]),
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
                        gradient: .init(colors: [theme.colors.accent]),
                        center: .center
                    ),
                    style: style
                )
                .rotationEffect(Angle(degrees: animate ? 360 : 0))
                .animation(.linear(duration: 0.7).repeatForever(autoreverses: false), value: animate)
                .frame(width: 100, height: 50)

            Spacer()
        }
        .padding(theme.spacing.medium)
        .edgesIgnoringSafeArea(.all)
        .onAppear {
            animate.toggle()
        }
    }
}

#Preview {
    LoadingIndicatorView()
}
