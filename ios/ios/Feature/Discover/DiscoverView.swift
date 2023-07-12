import SwiftUI
import TvManiac
import os.log

struct DiscoverView: View {
    
    @ObservedObject var viewModel: DiscoverShowsViewModel = DiscoverShowsViewModel()
    
    var body: some View {
        NavigationView {
            
            VStack {
                
                switch viewModel.showState {
                case is Loading:
                    LoadingIndicatorView()
                        .frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height,  alignment: .center)
                case is DataLoaded:
                    let state = viewModel.showState as! DataLoaded
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
        .onAppear { viewModel.startStateMachine() } }
    
    
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
