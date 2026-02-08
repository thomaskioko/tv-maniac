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

    func makeUIViewController(context: Context) -> SwipeBackViewController {
        SwipeBackViewController(coordinator: context.coordinator)
    }

    func updateUIViewController(_ uiViewController: SwipeBackViewController, context: Context) {
        uiViewController.coordinator = context.coordinator
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(onSwipe: onSwipe)
    }

    final class SwipeBackViewController: UIViewController {
        var coordinator: Coordinator?

        init(coordinator: Coordinator) {
            self.coordinator = coordinator
            super.init(nibName: nil, bundle: nil)
        }

        @available(*, unavailable)
        required init?(coder _: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }

        override func viewWillAppear(_ animated: Bool) {
            super.viewWillAppear(animated)
            guard let navigationController else { return }
            navigationController.interactivePopGestureRecognizer?.isEnabled = true
            navigationController.interactivePopGestureRecognizer?.delegate = coordinator
        }
    }

    class Coordinator: NSObject, UIGestureRecognizerDelegate {
        let onSwipe: () -> Void

        init(onSwipe: @escaping () -> Void) {
            self.onSwipe = onSwipe
        }

        func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
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
