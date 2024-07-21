//
//  CustomViewController.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/20/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

class SwipeBackViewController: UIViewController {
    private var animator: UIViewPropertyAnimator?
    private var rootView: RootView
    private var hostingController: UIHostingController<RootView>
    private let navigator: Navigator
    private var previousViewController: UIViewController?
    private var snapshotView: UIView?

    init(navigator: Navigator) {
        self.navigator = navigator
        self.rootView = RootView(navigator: navigator)
        self.hostingController = UIHostingController(rootView: self.rootView)
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupHostingController()
        setupBackGesture()
    }

    private func setupHostingController() {
        addChild(hostingController)
        view.addSubview(hostingController.view)
        hostingController.didMove(toParent: self)
        hostingController.view.frame = view.bounds
        hostingController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    }

    private func setupBackGesture() {
        let panGesture = UIPanGestureRecognizer(target: self, action: #selector(handlePanGesture(_:)))
        view.addGestureRecognizer(panGesture)
    }

    @objc private func handlePanGesture(_ gestureRecognizer: UIPanGestureRecognizer) {
        let translation = gestureRecognizer.translation(in: view)
        let progress = max(0, min(1, translation.x / view.bounds.width))

        switch gestureRecognizer.state {
        case .began:
            startInteractiveTransition()
        case .changed:
            updateInteractiveTransition(progress)
        case .ended, .cancelled:
            if progress > 0.5 || gestureRecognizer.velocity(in: view).x > 300 {
                finishInteractiveTransition()
                navigator.onBackClicked()
            } else {
                cancelInteractiveTransition()
            }
        default:
            break
        }
    }

    private func startInteractiveTransition() {
        guard let previousScreen = navigator.getPreviousScreen() else { return }

        snapshotView = hostingController.view.snapshotView(afterScreenUpdates: true)
        view.addSubview(snapshotView!)

        previousViewController = UIHostingController(rootView: ChildView(screen: previousScreen))
        if let previousView = previousViewController?.view {
            addChild(previousViewController!)
            view.insertSubview(previousView, belowSubview: snapshotView!)
            previousView.frame = view.bounds
            previousViewController?.didMove(toParent: self)
        }
    }

    private func updateInteractiveTransition(_ progress: CGFloat) {
        snapshotView?.frame.origin.x = progress * view.bounds.width
    }

    private func finishInteractiveTransition() {
        UIView.animate(withDuration: 0.3, animations: {
            self.snapshotView?.frame.origin.x = self.view.bounds.width
        }, completion: { _ in
            self.completeTransition()
        })
    }

    private func cancelInteractiveTransition() {
        UIView.animate(withDuration: 0.3, animations: {
            self.snapshotView?.frame.origin.x = 0
        }, completion: { _ in
            self.previousViewController?.willMove(toParent: nil)
            self.previousViewController?.view.removeFromSuperview()
            self.previousViewController?.removeFromParent()
            self.previousViewController = nil
            self.snapshotView?.removeFromSuperview()
            self.snapshotView = nil
        })
    }

    private func completeTransition() {
        hostingController.willMove(toParent: nil)
        hostingController.view.removeFromSuperview()
        hostingController.removeFromParent()

        if let previousView = previousViewController?.view {
            previousView.frame = view.bounds
            previousView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        }

        snapshotView?.removeFromSuperview()
        snapshotView = nil

        updateRootView()
    }

    private func updateRootView() {
        rootView = RootView(navigator: navigator)
        hostingController = UIHostingController(rootView: rootView)
        setupHostingController()
    }
}
