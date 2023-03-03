import SwiftUI
import TvManiac
import os.log

struct DiscoverView: View {

    @ObservedObject var viewModel: DiscoverShowsViewModel = DiscoverShowsViewModel(showState: Loading_())

    var body: some View {
        NavigationView {

            VStack {

                switch viewModel.showState {
                case is Loading_:
                    LoadingIndicatorView()
						.frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height,  alignment: .center)
                case is LoadingError:
                    let state = viewModel.showState as! LoadingError
                    ErrorView(errorMessage: state.message)
                case is ShowsLoaded:
                    let state = viewModel.showState as! ShowsLoaded
                    BodyContentView(contentState: state)
                default:
                    fatalError("Unhandled case: \(viewModel.showState)")
                }
            }
			.background(Color.background)
			.ignoresSafeArea()
			.navigationBarHidden(true)
        }
                .background(Color.background)
                .navigationViewStyle(StackNavigationViewStyle())
                .onAppear { viewModel.startStateMachine() }
    }


}

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
