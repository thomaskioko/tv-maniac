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
        return SwipeBackViewController(navigator: component.navigator)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
