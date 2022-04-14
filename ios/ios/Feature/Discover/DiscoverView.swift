import SwiftUI
import TvManiac

struct DiscoverView: View {

    @ObservedObject var observable = ObservableViewModel<DiscoverShowsViewModel, DiscoverShowsState>(
            viewModel: DiscoverShowsViewModel()
    )


    var body: some View {
        NavigationView {

            VStack {
                if observable.state is DiscoverShowsState.InProgress {
                    LoadingIndicatorView()
                } else if observable.state is DiscoverShowsState.Error {
                    //TODO:: Show Error
                    EmptyView()
                } else if observable.state is DiscoverShowsState.Success {

                    let result = observable.state as? DiscoverShowsState.Success

                    BodyContentView(showResult: result!.data)

                }
            }
                    .ignoresSafeArea()
                    .navigationBarHidden(true)
        }
                .onAppear {
                    observable.viewModel.attach()
                }
                .onDisappear {
                    observable.viewModel.detach()
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
