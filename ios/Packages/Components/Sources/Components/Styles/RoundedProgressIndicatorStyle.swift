import DesignSystem
import SwiftUI

public struct RoundedRectProgressViewStyle: ProgressViewStyle {
    @Environment(\.appTheme) private var theme

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
        let isComplete = (configuration.fractionCompleted ?? 0) >= 1
        let resolvedFillColor = accentColor ?? theme.colors.secondary
        let resolvedTrackColor = accentColor ?? (isComplete ? theme.colors.success : theme.colors.secondary)

        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                Rectangle()
                    .frame(height: progressIndicatorHeight)
                    .foregroundColor(resolvedTrackColor.opacity(0.5))

                Rectangle()
                    .frame(
                        width: CGFloat(configuration.fractionCompleted ?? 0) * geometry.size.width,
                        height: progressIndicatorHeight
                    )
                    .foregroundColor(resolvedFillColor)
            }
        }
        .frame(height: progressIndicatorHeight)
    }
}

#Preview {
    VStack {
        Spacer()

        ProgressView(
            value: CGFloat(0.4),
            total: 1
        )
        .progressViewStyle(RoundedRectProgressViewStyle(progressIndicatorHeight: 6))

        ProgressView(
            value: CGFloat(1),
            total: 1
        )
        .progressViewStyle(RoundedRectProgressViewStyle(progressIndicatorHeight: 6))

        Spacer()
    }
    .padding()
}
