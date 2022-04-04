//
//  ShowDetailView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac
import Kingfisher

struct ShowDetailView: View {

    @ObservedObject var observable = ObservableViewModel<ShowDetailsViewModel, ShowDetailUiViewState>(
            viewModel: ShowDetailsViewModel()
    )


    var showId: Int64

    init(showId: Int64) {
        self.showId = showId
    }

    var body: some View {
        ScrollView(.vertical, showsIndicators: false) {

            HeaderImageView(viewState: observable.state)

            ShowBodyView(viewState: observable.state)
                    .offset(y: -170)


        }
                .onAppear {
                    observable.viewModel.attach()
                    observable.viewModel.dispatch(action: ShowDetailAction.LoadShowDetails(showId: self.showId))
                }
                .onDisappear {
                    observable.viewModel.detach()
                }
                .edgesIgnoringSafeArea(.all)
                .background(Color.background)
    }
}

struct ShowDetailView_Previews: PreviewProvider {
    static var previews: some View {
        ShowDetailView(showId: 1234)
    }
}
