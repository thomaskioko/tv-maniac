//
//  ShowDetailView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct ShowDetailView: View {
	
	@ObservedObject var observable = ObservableViewModel<ShowDetailsViewModel, ShowDetailUiViewState>(
		viewModel: ShowDetailsViewModel()
	)
	
	var showId: Int64
	
	init(showId: Int64) {
		self.showId = showId
	}
	
	var body: some View {
		ZStack {
			
			ScrollView(.vertical, showsIndicators: false) {
				
				HeaderView(viewState: observable.state)
					.frame(width: PosterStyle.Size.max.width(), height: PosterStyle.Size.tv.height())
				
				
				ShowBodyView(viewState: observable.state)
				
			}
			
		}
		.onAppear {
			observable.viewModel.attach()
			observable.viewModel.dispatch(action: ShowDetailAction.LoadShowDetails(showId: self.showId))
		}
		.onDisappear {
			observable.viewModel.detach()
		}
		.background(Color.background)
		.edgesIgnoringSafeArea(.all)
	}
}

struct ShowDetailView_Previews: PreviewProvider {
	static var previews: some View {
		ShowDetailView(showId: 1234)
	}
}
