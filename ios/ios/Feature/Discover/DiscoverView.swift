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
                case is LoadShows:
                    LoadingIndicatorView()
                case is LoadingError:
                    let state = viewModel.showState as! LoadingError
                    ErrorView(errorMessage: state.errorMessage ?? "Opps!! Something went wrong")
                case is ShowsLoaded:
                    let state = viewModel.showState as! ShowsLoaded
                    BodyContentView(contentState: state)
                default:
                    fatalError("Unhandled case: \(viewModel.showState)")
                }
            }
                    .ignoresSafeArea()
                    .navigationBarHidden(true)
        }
                .onAppear {
                    viewModel.startStateMachine()
                }
                .accentColor(Color.background)
                .navigationViewStyle(StackNavigationViewStyle())
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
