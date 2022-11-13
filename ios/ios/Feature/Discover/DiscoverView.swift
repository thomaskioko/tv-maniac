import SwiftUI
import TvManiac
import os.log

struct DiscoverView: View {
	
	@ObservedObject var viewModel: DiscoverShowsViewModel = DiscoverShowsViewModel(showState: FetchShows())
	
	var body: some View {
		NavigationView {
			
			VStack {

				if viewModel.showState is FetchShows {
					LoadingIndicatorView()
				} else if viewModel.showState is LoadShows {
					LoadingIndicatorView()
				}else if let state = viewModel.showState as? LoadingError {
					ErrorView(errorMessage:  state.errorMessage ?? "Opps!! Something went wrong")
				} else if let state = viewModel.showState as? ShowsLoaded {
					
					if state.result.updateState == ShowUpdateState.empty {
						//TODO:: Show empty view
					} else if state.result.updateState == ShowUpdateState.error {
						//TODO:: Show "Snackbar"
					}
					BodyContentView(contentState: state)
				}
			}
			.ignoresSafeArea()
			.navigationBarHidden(true)
		}  .onAppear {

			viewModel.startStateMachine()
		}
		.accentColor(Color.background)
		.navigationViewStyle(StackNavigationViewStyle())
	}
	
}


// Screen Bounds...
extension View {
	func getRect() -> CGRect {
		UIScreen.main.bounds
	}
}


struct DiscoverView_Previews: PreviewProvider {

	static var previews: some View {
		DiscoverView()

		DiscoverView()
			.preferredColorScheme(.dark)
	}
}
