//
//  TransparentBlurView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/25/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct TransparentBlurView: UIViewRepresentable {
    let style: UIBlurEffect.Style

    func makeUIView(context: Context) -> UIVisualEffectView {
        return UIVisualEffectView(effect: UIBlurEffect(style: style))
    }

    func updateUIView(_ uiView: UIVisualEffectView, context: Context) {
        uiView.effect = UIBlurEffect(style: style)
    }
}

