import SwiftUI


struct HomeUIView: View {
    
    
    var body: some View {
        
        TabView {
            DiscoverView()
                .font(.system(size: 30, weight: .bold, design: .rounded))
                .tabItem {
                    Image(systemName: "film")
                    Text("Discover")
                    
                }
            
            SearchView()
                .font(.system(size: 30, weight: .bold, design: .rounded))
                .tabItem {
                    Image(systemName: "magnifyingglass")
                    Text("Search")
                }
            
            WatchlistView()
                .font(.system(size: 30, weight: .bold, design: .rounded))
                .tabItem {
                    Image(systemName: "list.bullet.below.rectangle")
                    Text("Watchlist")
                }
            
            ProfileView()
                .font(.system(size: 30, weight: .bold, design: .rounded))
                .tabItem {
                    Image(systemName: "person.circle")
                    Text("Profile")
                }
        }
        .onAppear {
            UITabBar.appearance().barTintColor = UIColor(Color("TabBackgroundColor"))
        }
        .accentColor(Color.accent)
    }
}

struct HomeUIView_Previews: PreviewProvider {
    
    
    static var previews: some View {
        HomeUIView()
        
        HomeUIView()
            .preferredColorScheme(.dark)
    }
}
