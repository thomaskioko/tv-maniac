#if DEBUG
    import SwiftUI
    import UIKit

    private final class WindowGestureHost: UIView {
        var coordinator: DebugTapGestureView.Coordinator?

        override func didMoveToWindow() {
            super.didMoveToWindow()
            coordinator?.attach(to: window)
        }
    }

    struct DebugTapGestureView: UIViewRepresentable {
        let onTripleTap: () -> Void

        func makeUIView(context: Context) -> UIView {
            let view = WindowGestureHost()
            view.isHidden = true
            view.coordinator = context.coordinator
            return view
        }

        func updateUIView(_: UIView, context _: Context) {}

        func makeCoordinator() -> Coordinator {
            Coordinator(onTripleTap: onTripleTap)
        }

        final class Coordinator: NSObject {
            let onTripleTap: () -> Void
            private var gesture: UITapGestureRecognizer?
            private weak var attachedWindow: UIWindow?

            init(onTripleTap: @escaping () -> Void) {
                self.onTripleTap = onTripleTap
            }

            func attach(to window: UIWindow?) {
                if let existing = gesture, let previous = attachedWindow {
                    previous.removeGestureRecognizer(existing)
                }

                guard let window else {
                    gesture = nil
                    attachedWindow = nil
                    return
                }

                let tap = UITapGestureRecognizer(target: self, action: #selector(handleTap))
                tap.numberOfTapsRequired = 3
                tap.numberOfTouchesRequired = 2
                tap.cancelsTouchesInView = false
                tap.delaysTouchesBegan = false
                tap.delaysTouchesEnded = false

                window.addGestureRecognizer(tap)
                gesture = tap
                attachedWindow = window
            }

            @objc func handleTap() {
                let generator = UINotificationFeedbackGenerator()
                generator.notificationOccurred(.success)
                onTripleTap()
            }

            deinit {
                if let gesture, let attachedWindow {
                    attachedWindow.removeGestureRecognizer(gesture)
                }
            }
        }
    }

    public extension View {
        func debugTapGesture(onTripleTap: @escaping () -> Void) -> some View {
            background(DebugTapGestureView(onTripleTap: onTripleTap))
        }
    }
#endif
