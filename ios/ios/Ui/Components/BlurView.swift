//
// Created by Thomas Kioko on 05.04.22.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI

struct BlurView: UIViewRepresentable {

    func makeUIView(context: Context) -> UIVisualEffectView {
        UIVisualEffectView(effect: UIBlurEffect(style: .systemChromeMaterialDark))
    }

    func updateUIView(_ uiView: UIVisualEffectView, context: Context) {

    }
}

