//
//  DecomposeView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/20/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import TvManiac

struct DecomposeView: UIViewControllerRepresentable {
    let component: IosViewPresenterComponent

    func makeUIViewController(context: Context) -> UIViewController {
        let viewController = UIViewController()
        let hostingController = UIHostingController(rootView: RootView(navigator: component.navigator))
        viewController.addChild(hostingController)
        viewController.view.addSubview(hostingController.view)
        hostingController.didMove(toParent: viewController)
        hostingController.view.frame = viewController.view.bounds
        hostingController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]

        // Add back gesture recognizer
        let backGesture = UIScreenEdgePanGestureRecognizer(target: context.coordinator, action: #selector(Coordinator.handleBackGesture(_:)))
        backGesture.edges = .left
        viewController.view.addGestureRecognizer(backGesture)

        return viewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(component: component)
    }

    class Coordinator: NSObject {
        let component: IosViewPresenterComponent

        init(component: IosViewPresenterComponent) {
            self.component = component
        }

        @objc func handleBackGesture(_ gestureRecognizer: UIScreenEdgePanGestureRecognizer) {
            if gestureRecognizer.state == .ended {
                component.navigator.onBackClicked()
            }
        }
    }
}
