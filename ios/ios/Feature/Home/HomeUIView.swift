import SwiftUI


struct HomeUIView: View {

    
    var body: some View {
        
        TabView {
            DiscoverView()
                .setTabItem("Discover", "film")
                .setTabBarBackground(.init(.ultraThickMaterial))
               
            
            SearchView()
                .setTabItem("Search", "magnifyingglass")
                .setTabBarBackground(.init(.ultraThickMaterial))
            
            WatchlistView()
                .setTabItem("Watchlist", "list.bullet.below.rectangle")
                .setTabBarBackground(.init(.ultraThickMaterial))
            
            
            ProfileView()
                .setTabItem("Profile", "person.circle")
                .setTabBarBackground(.init(.ultraThickMaterial))
            
        }
        .tint(Color.accent_color)
    }
}

/// Custom View Modifier's
extension View {
    @ViewBuilder
    func setTabItem(_ title: String, _ icon: String) -> some View {
        self
            .tabItem {
                Image(systemName: icon)
                Text(title)
            }
    }
    
    @ViewBuilder
    func setTabBarBackground(_ style: AnyShapeStyle) -> some View {
        self
            .toolbarBackground(.visible, for: .tabBar)
            .toolbarBackground(style, for: .tabBar)
    }
    
    @ViewBuilder
    func hideTabBar(_ status: Bool) -> some View {
        self
            .toolbar(status ? .hidden : .visible, for: .tabBar)
    }
}

struct HomeUIView_Previews: PreviewProvider {
    
    
    static var previews: some View {
        HomeUIView()
        
        HomeUIView()
            .preferredColorScheme(.dark)
    }
}
