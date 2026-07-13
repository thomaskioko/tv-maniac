import UIKit

/// Fires device haptics, gated by an explicit `isEnabled` flag supplied by the caller.
///
/// The design system does not read the preference itself — callers pass it (typically from
/// `\.hapticFeedbackEnabled` in the environment) so gating stays a pure function of inputs. Route
/// every haptic call site through this so turning the preference off silences them all.
public enum Haptics {
    public static func impact(
        isEnabled: Bool,
        style: UIImpactFeedbackGenerator.FeedbackStyle = .light
    ) {
        guard isEnabled else { return }
        UIImpactFeedbackGenerator(style: style).impactOccurred()
    }

    public static func notification(
        isEnabled: Bool,
        type: UINotificationFeedbackGenerator.FeedbackType
    ) {
        guard isEnabled else { return }
        UINotificationFeedbackGenerator().notificationOccurred(type)
    }
}
