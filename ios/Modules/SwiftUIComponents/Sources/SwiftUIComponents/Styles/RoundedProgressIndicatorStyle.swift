import SwiftUI

public struct RoundedRectProgressViewStyle: ProgressViewStyle {
  private let progressIndicatorHeight: CGFloat

  public init(progressIndicatorHeight: CGFloat = 8) {
    self.progressIndicatorHeight = progressIndicatorHeight
  }

  public func makeBody(configuration: Configuration) -> some View {
    ZStack(alignment: .leading) {
      Rectangle()
        .frame(height: progressIndicatorHeight)
        .foregroundColor(.accent.opacity(0.2))
        .overlay(Color.accent.opacity(0.2))

      Rectangle()
        .frame(
          width: CGFloat(configuration.fractionCompleted ?? 0) * DimensionConstants.screenWidth,
          height: progressIndicatorHeight
        )
        .foregroundColor(.accent)
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
