import SwiftUI

public struct SegmentedProgressBar: View {
    @Theme private var theme

    private let segmentProgress: [Float]
    private let height: CGFloat
    private let segmentGap: CGFloat

    public init(
        segmentProgress: [Float],
        height: CGFloat = 6,
        segmentGap: CGFloat = 4
    ) {
        self.segmentProgress = segmentProgress
        self.height = height
        self.segmentGap = segmentGap
    }

    public var body: some View {
        if segmentProgress.isEmpty {
            EmptyView()
        } else {
            HStack(spacing: segmentGap) {
                ForEach(Array(segmentProgress.enumerated()), id: \.offset) { _, progress in
                    ProgressSegment(
                        progress: progress,
                        height: height,
                        accentColor: theme.colors.accent,
                        trackColor: theme.colors.accent.opacity(0.3)
                    )
                }
            }
        }
    }
}

private struct ProgressSegment: View {
    let progress: Float
    let height: CGFloat
    let accentColor: Color
    let trackColor: Color

    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: height / 2)
                    .fill(trackColor)
                    .frame(height: height)

                RoundedRectangle(cornerRadius: height / 2)
                    .fill(accentColor)
                    .frame(
                        width: CGFloat(min(max(progress, 0), 1)) * geometry.size.width,
                        height: height
                    )
            }
        }
        .frame(height: height)
    }
}

#Preview {
    VStack(spacing: 20) {
        SegmentedProgressBar(
            segmentProgress: [1.0, 0.5, 0]
        )

        SegmentedProgressBar(
            segmentProgress: [0.75]
        )

        SegmentedProgressBar(
            segmentProgress: [1.0, 1.0, 1.0, 1.0, 1.0]
        )
    }
    .padding()
    .environment(\.tvManiacTheme, DarkTheme())
}
