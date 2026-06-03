import DesignSystem
import SwiftUI
import UIKit

/// Left-edge swipe-back gesture for pushed screens.
///
/// Default mode enables the navigation controller's native interactive pop, which drives the
/// `NavigationStack` path binding. Set `handledByPresenter` for screens whose back action is owned
/// by their presenter (for example Settings, which navigates between internal pages before leaving
/// the stack): the native pop is blocked and a left-edge pan dispatches `onSwipe` instead, so the
/// swipe matches the in-app back button.
public struct SwipeBackGesture: ViewModifier {
    private let handledByPresenter: Bool
    private let onSwipe: () -> Void

    public init(handledByPresenter: Bool = false, onSwipe: @escaping () -> Void) {
        self.handledByPresenter = handledByPresenter
        self.onSwipe = onSwipe
    }

    public func body(content: Content) -> some View {
        content
            .background(
                SwipeBackGestureHandler(handledByPresenter: handledByPresenter, onSwipe: onSwipe)
            )
    }
}

private struct SwipeBackGestureHandler: UIViewControllerRepresentable {
    let handledByPresenter: Bool
    let onSwipe: () -> Void

    func makeUIViewController(context: Context) -> SwipeBackViewController {
        SwipeBackViewController(handledByPresenter: handledByPresenter, coordinator: context.coordinator)
    }

    func updateUIViewController(_ uiViewController: SwipeBackViewController, context: Context) {
        uiViewController.coordinator = context.coordinator
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(handledByPresenter: handledByPresenter, onSwipe: onSwipe)
    }

    final class SwipeBackViewController: UIViewController {
        private let handledByPresenter: Bool
        var coordinator: Coordinator?
        private weak var edgePan: UIScreenEdgePanGestureRecognizer?

        init(handledByPresenter: Bool, coordinator: Coordinator) {
            self.handledByPresenter = handledByPresenter
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

            guard handledByPresenter, edgePan == nil else { return }
            let recognizer = UIScreenEdgePanGestureRecognizer(
                target: coordinator,
                action: #selector(Coordinator.handleEdgePan(_:))
            )
            recognizer.edges = .left
            recognizer.delegate = coordinator
            navigationController.view.addGestureRecognizer(recognizer)
            coordinator?.edgePan = recognizer
            edgePan = recognizer
        }

        override func viewWillDisappear(_ animated: Bool) {
            super.viewWillDisappear(animated)
            if let edgePan {
                edgePan.view?.removeGestureRecognizer(edgePan)
            }
            edgePan = nil
        }
    }

    class Coordinator: NSObject, UIGestureRecognizerDelegate {
        private let handledByPresenter: Bool
        let onSwipe: () -> Void
        weak var edgePan: UIScreenEdgePanGestureRecognizer?

        init(handledByPresenter: Bool, onSwipe: @escaping () -> Void) {
            self.handledByPresenter = handledByPresenter
            self.onSwipe = onSwipe
        }

        @objc func handleEdgePan(_ recognizer: UIScreenEdgePanGestureRecognizer) {
            if recognizer.state == .recognized {
                onSwipe()
            }
        }

        func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
            if handledByPresenter {
                // Allow only our edge-pan; block the native pop so a swipe does not pop the
                // whole screen off the stack and instead routes through the presenter.
                return gestureRecognizer === edgePan
            }
            var responder: UIResponder? = gestureRecognizer.view
            while let current = responder {
                if let nav = current as? UINavigationController {
                    return nav.viewControllers.count > 1
                }
                responder = current.next
            }
            return true
        }

        func gestureRecognizer(_: UIGestureRecognizer,
                               shouldRecognizeSimultaneouslyWith _: UIGestureRecognizer) -> Bool
        {
            true
        }
    }
}

public extension View {
    func swipeBackGesture(handledByPresenter: Bool = false, onSwipe: @escaping () -> Void) -> some View {
        modifier(SwipeBackGesture(handledByPresenter: handledByPresenter, onSwipe: onSwipe))
    }
}
