//
//  MoreShowsView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 02.01.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct MoreShowsView: View {
    
    private let presenter: MoreShowsPresenter

    @ObservedObject
    private var uiState: StateFlow<MoreShowsState>

    @State
    private var query = String()

    init(presenter: MoreShowsPresenter) {
        self.presenter = presenter
        self.uiState = StateFlow<MoreShowsState>(presenter.state)
    }
    
    var body: some View {
        NavigationStack {
            VStack {
                empty
            }
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button
                    { 
                        presenter.dispatch(action: MoreBackClicked())
                    } label: {
                        Text(uiState.value?.categoryTitle ?? "")
                    }
                    .buttonStyle(CircleButtonStyle(imageName: "arrow.backward"))
                    .padding(.top)
                }
            }
        }
    }

    @ViewBuilder
    private var empty: some View {
        if #available(iOS 17.0, *) {
            ContentUnavailableView(
                "Under Construction",
                systemImage: "figure.walk.motion.trianglebadge.exclamationmark"
            )
            .padding()
            .multilineTextAlignment(.center)
            .font(.callout)
            .foregroundColor(Color.accent)
        } else {
            FullScreenView(
                systemName: "figure.walk.motion.trianglebadge.exclamationmark",
                message: "Under Construction"
            )
        }
    }
}

private struct DimensionConstants {
    static let posterColumns = [GridItem(.adaptive(minimum: 100), spacing: 8)]
    static let spacing: CGFloat = 4
}
