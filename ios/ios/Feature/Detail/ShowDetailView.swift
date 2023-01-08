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
	
	@ObservedObject var viewModel: ShowDetailsViewModel = ShowDetailsViewModel(
		detailState: ShowDetailsStateLoading()
	)
	
	@SwiftUI.State var offset: CGFloat = 0
	@SwiftUI.State var titleOffset: CGFloat = 0
	@SwiftUI.State var size: CGSize = CGSize(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)

	
	var showId: Int32
	let maxHeight = CGFloat(520)
	
	init(showId: Int32) {
		self.showId = showId
	}
	
	var body: some View {
		
		VStack {
			switch viewModel.detailState {
			case is ShowDetailsStateLoading:
				LoadingIndicatorView()
					.frame(maxWidth: size.width, maxHeight: size.height,  alignment: .center)
			case is ShowDetailsStateShowDetailsLoaded:
				let state = viewModel.detailState as! ShowDetailsStateShowDetailsLoaded
				
				ScrollView(.vertical, showsIndicators: false) {
					VStack {
						ArtWork(state: state)
						
						ShowBodyView(detailLoadedState: state)
					}
				}
				.coordinateSpace(name: "SCROLL")
				
			case is ShowDetailsStateShowDetailsError:
				let state = viewModel.detailState as! ShowDetailsStateShowDetailsError
				
				ErrorView(errorMessage: state.errorMessage)
			default:
				let _ = print("Unhandled case: \(viewModel.detailState)")
			}
		}
		.overlay(alignment: .top){
			if let state = viewModel.detailState as? ShowDetailsStateShowDetailsLoaded {
				TopNavBarView(showTitle: state.show.title)
			} else {
				TopNavBarView(showTitle: "")
			}
		}
		.background(Color.background)
		.navigationBarHidden(true)
		.ignoresSafeArea()
		.onAppear { viewModel.startStateMachine(action: LoadShowDetails(traktId: showId)) }
		.onDisappear { viewModel.dismiss() }
	}
	
	@ViewBuilder
	func ArtWork(state: ShowDetailsStateShowDetailsLoaded) -> some View {
		let height = size.height * 0.45
		
		GeometryReader { proxy in
			let size = proxy.size
			let minY = proxy.frame(in: .named("SCROLL")).minY
			let progress = minY / (height * (minY > 0 ? 0.5 : 0.8))
			
			ShowPosterImage(
				posterSize: .max,
				imageUrl: state.show.backdropImageUrl
			)
			.aspectRatio(contentMode: .fill)
			.frame(width: size.width, height: size.height + (minY > 0 ? minY : 0))
			.clipped()
			.overlay( content : {
				ZStack(alignment: .bottom) {
					
					Rectangle()
						.fill(
							.linearGradient(colors: [
								Color.background,
								.clear,
								Color.background.opacity(0 - progress),
								Color.background.opacity(0.1 - progress),
								Color.background.opacity(0.3 - progress),
								Color.background.opacity(0.5 - progress),
								Color.background.opacity(0.8 - progress),
								Color.background.opacity(1),
							], startPoint: .top, endPoint: .bottom)
						)
					
					//Header Content
					HeaderContentView(show: state.show)
						.opacity(1 + (progress > 0 ? -progress : progress))
						.padding(.horizontal,16)
					// Moving With ScrollView
						.offset(y: minY < 0 ? minY : 0)
				}
			})
			.offset(y: -minY)
		}
		.frame(height: maxHeight)
	}
	
	
	@ViewBuilder
	func TopNavBarView(showTitle: String)->some View{
		GeometryReader{proxy in
			let minY = proxy.frame(in: .named("SCROLL")).minY
			let height = maxHeight * 0.45
			let progress = minY / (height * (minY > 0 ? 0.5 : 0.8))
			let titleProgress = minY / height
			
			TopNavBar(
				titleProgress: titleProgress,
				title: showTitle
			)
			.padding(.top, 45)
			.padding([.horizontal,],15)
			.background(content: {
				Color.background
					.opacity(-progress > 1 ? 1 : 0)
			})
			.offset(y: -minY)
		}
	}
	
	@ViewBuilder
	func HeaderContentView(show: Show) -> some View {
		
		VStack(spacing: 0){
			Text(show.title)
				.titleFont(size: 30)
				.foregroundColor(Color.text_color_bg)
				.lineLimit(1)
				.padding(.top, 8)
			
			Text(show.overview)
				.bodyFont(size: 18)
				.foregroundColor(Color.text_color_bg)
				.lineLimit(3)
				.padding(.top, 1)
			
			ShowInfoRow(show: show)
				.padding(.top, 5)
			
			GenresRowView(genres: show.genres)
				.padding(.top, 5)
			
			HStack(alignment: .center, spacing: 8) {
				
				BorderedButton(
					text: "Watch Trailer",
					systemImageName: "film.fill",
					color: .accent,
					borderColor: .grey_200,
					isOn: false,
					action: {
						//TODO:: Navigate to trailer view
					})
				
				BorderedButton(
					text: "Follow Show",
					systemImageName: "plus.app.fill",
					color: .accent,
					borderColor: .grey_200,
					isOn: false,
					action: {
						viewModel.dispatchAction(action: FollowShow(traktId: show.traktId, addToWatchList: show.isFollowed))
					}
				)
			}
			.padding(.bottom, 16)
			.padding(.top, 10)
		}
		
	}
}


struct ShowDetailView_Previews: PreviewProvider {
    static var previews: some View {
        ShowDetailView(showId: 1234)
    }
}
