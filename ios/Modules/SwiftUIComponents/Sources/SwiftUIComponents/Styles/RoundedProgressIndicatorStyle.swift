import SwiftUI

public struct RoundedRectProgressViewStyle: ProgressViewStyle {
    @Theme private var theme

    private let progressIndicatorHeight: CGFloat
    private let accentColor: Color?

    public init(
        progressIndicatorHeight: CGFloat = 8,
        accentColor: Color? = nil
    ) {
        self.progressIndicatorHeight = progressIndicatorHeight
        self.accentColor = accentColor
    }

    public func makeBody(configuration: Configuration) -> some View {
        let resolvedColor = accentColor ?? theme.colors.accent

        ZStack(alignment: .leading) {
            Rectangle()
                .frame(height: progressIndicatorHeight)
                .foregroundColor(resolvedColor.opacity(0.2))
                .overlay(resolvedColor.opacity(0.2))

            Rectangle()
                .frame(
                    width: CGFloat(configuration.fractionCompleted ?? 0) * DimensionConstants.screenWidth,
                    height: progressIndicatorHeight
                )
                .foregroundColor(resolvedColor)
        }
    }
}

private enum DimensionConstants {
    static let screenWidth = UIScreen.main.bounds.size.width
}

#Preview {
    VStack {
        Spacer()
        ProgressView(
            value: CGFloat(0.4),
            total: 1
        )
        .progressViewStyle(RoundedRectProgressViewStyle(progressIndicatorHeight: 6))

        Spacer()
    }
    .padding()
}
