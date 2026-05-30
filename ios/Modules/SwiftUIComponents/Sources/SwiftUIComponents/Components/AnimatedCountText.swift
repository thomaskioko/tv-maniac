import SwiftUI

/// `Text` that animates between numeric values using an odometer-style transition. Holds the final
/// value immediately so previews and snapshot tests stay deterministic; animation runs on change.
/// Honors Reduce Motion by updating without animation. Callers style it via `.textStyle(_:)`.
public struct AnimatedCountText: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    private let count: Int
    private let format: (Int) -> String

    @SwiftUI.State private var displayed: Int

    public init(count: Int, format: @escaping (Int) -> String = AnimatedCountText.grouped) {
        self.count = count
        self.format = format
        _displayed = SwiftUI.State(initialValue: count)
    }

    public var body: some View {
        Text(format(displayed))
            .contentTransition(.numericText(value: Double(displayed)))
            .monospacedDigit()
            .onChange(of: count) { _, newValue in
                if reduceMotion {
                    displayed = newValue
                } else {
                    withAnimation(.easeOut(duration: 0.28)) {
                        displayed = newValue
                    }
                }
            }
    }

    public static func grouped(_ value: Int) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        return formatter.string(from: NSNumber(value: value)) ?? "\(value)"
    }
}
