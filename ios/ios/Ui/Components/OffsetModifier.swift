//
// Created by Thomas Kioko on 07.04.22.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI

struct OffsetModifier: ViewModifier {

    @Binding var offset: CGFloat

    func body(content: Content) -> some View {

        content
                .overlay(
                        GeometryReader { proxy -> Color in
                            let minY = proxy.frame(in: .named("SCROLL")).minY
                            DispatchQueue.main.async {
                                self.offset = minY
                            }
                            return Color.clear
                        }
                        , alignment: .top
                )
    }
}