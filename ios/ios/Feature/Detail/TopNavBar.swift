//
// Created by Thomas Kioko on 07.04.22.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct TopNavBar: View {

    @Binding var offset: CGFloat
    var viewState: ShowDetailUiViewState
    var maxHeight: CGFloat
    var topEdge: CGFloat

    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>

    var body: some View {

        HStack(spacing: 15) {

            Button {
                presentationMode.wrappedValue.dismiss()
            } label: {
                Image(systemName: "arrow.left")
                        .font(.body.bold())
            }

            Spacer()

            VStack(alignment: .leading, spacing: 8) {
                Text(viewState.tvShow.title)
                        .titleFont(size: 24)
                        .foregroundColor(Color.text_color_bg)
                        .lineLimit(1)
                        .opacity(topBarTitleOpacity())
            }

            Spacer()

        }
                .navigationBarBackButtonHidden(true)

    }

    func topBarTitleOpacity() -> CGFloat {
        let progress = -(offset + 380) / (maxHeight - (80 + topEdge))

        return progress
    }
}