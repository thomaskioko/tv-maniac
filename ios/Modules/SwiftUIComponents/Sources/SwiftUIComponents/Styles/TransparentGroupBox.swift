//
//  TransparentGroupBox.swift
//
//
//  Created by Thomas Kioko on 9/8/24.
//

import SwiftUI

struct TransparentGroupBox: GroupBoxStyle {
    func makeBody(configuration: Configuration) -> some View {
        VStack {
            HStack {
                configuration.label
                    .font(.headline)
                    .foregroundColor(.primary)
                Spacer()
            }

            configuration.content
                .foregroundColor(.primary)
        }
        .padding()
        .background {
            ZStack {
                Rectangle().fill(.background)
            }
            .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
            .shadow(radius: 1)
        }
    }
}
