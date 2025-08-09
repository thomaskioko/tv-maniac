import SwiftUI
import UIKit

public struct SwipeBackGesture: ViewModifier {
    private let onSwipe: () -> Void

    public init(onSwipe: @escaping () -> Void) {
        self.onSwipe = onSwipe
    }

    public func body(content: Content) -> some View {
        content
            .background(
                SwipeBackGestureHandler(onSwipe: onSwipe)
            )
    }
}

private struct SwipeBackGestureHandler: UIViewControllerRepresentable {
    let onSwipe: () -> Void

    func makeUIViewController(context _: Context) -> UIViewController {
        let controller = UIViewController()
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Enable the interactive pop gesture even when the back button is hidden
        DispatchQueue.main.async {
            if let navigationController = uiViewController.navigationController {
                navigationController.interactivePopGestureRecognizer?.isEnabled = true
                navigationController.interactivePopGestureRecognizer?.delegate = context.coordinator
            }
        }
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(onSwipe: onSwipe)
    }

    class Coordinator: NSObject, UIGestureRecognizerDelegate {
        let onSwipe: () -> Void

        init(onSwipe: @escaping () -> Void) {
            self.onSwipe = onSwipe
        }

        func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
            // Allow the gesture if we're not at the root of the navigation stack
            if let nav = gestureRecognizer.view as? UINavigationController {
                return nav.viewControllers.count > 1
            }
            return true
        }

        func gestureRecognizer(_: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith _: UIGestureRecognizer) -> Bool {
            true
        }
    }
}

public extension View {
    func swipeBackGesture(onSwipe: @escaping () -> Void) -> some View {
        modifier(SwipeBackGesture(onSwipe: onSwipe))
    }
}
