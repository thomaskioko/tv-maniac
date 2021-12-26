import SwiftUI
import TvManiac

struct HomeUIView: View {
	private let networkModule: NetworkModule
	private let databaseModule: DatabaseModule
	private let repositoryModule: RepositoryModule
	private let datasourceModule: DataSourceModule
	
	init(
		networkModule: NetworkModule,
		databaseModule: DatabaseModule
	){
		self.networkModule = networkModule
		self.databaseModule = databaseModule
		repositoryModule = RepositoryModule(
			networkModule: self.networkModule,
			databaseModule: self.databaseModule
		)
		datasourceModule = DataSourceModule(repositoryModule: repositoryModule)
	}

	var body: some View {
	
		TabView {
			DiscoverView(
				networkModule: networkModule,
				databaseModule: databaseModule
			)
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
		}
		.onAppear {
			UITabBar.appearance().barTintColor = UIColor(Color("TabBackgroundColor"))
		}
		.accentColor(Color("AccentColor"))
	}
}

struct HomeUIView_Previews: PreviewProvider {
	static private var networkModule = NetworkModule()
	static private var databaseModule = DatabaseModule()
	
    static var previews: some View {
		HomeUIView(networkModule: networkModule, databaseModule: databaseModule)
		
		HomeUIView(networkModule: networkModule, databaseModule: databaseModule)
			.preferredColorScheme(.dark)
    }
}
