//
//  DiscoverVIewModel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 31.10.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation
import Combine
import shared
import KMPNativeCoroutinesCombine

final class DiscoverViewModel : ObservableObject {
	
	private let logger = Logger(className: "DiscoverViewModel")
	
	private var showsCancellable: AnyCancellable?
	
	private let networkModule: NetworkModule
	private let databaseModule: DatabaseModule
	private let repositoryModule: RepositoryModule
	
	let getTrendingShowsInteractor: GetTrendingShowsInteractor
	
	@Published var state : DiscoverShowState = DiscoverShowState.companion.Empty
	@Published var trendingDataResult = [TrendingShowData]()
	
	init(
		networkModule: NetworkModule,
		databaseModule: DatabaseModule,
		getTrendingShowsInteractor: GetTrendingShowsInteractor
	){
		self.networkModule = networkModule
		self.databaseModule = databaseModule
		self.repositoryModule = RepositoryModule(
			networkModule: self.networkModule,
			databaseModule: self.databaseModule
		)
		self.getTrendingShowsInteractor = getTrendingShowsInteractor
		
	}
	
	
	func startObservingTrendingShows(){
		let showCategory :[ShowCategory] = [ShowCategory.featured, ShowCategory.thisWeek, ShowCategory.topRated, ShowCategory.popular]
		
		showsCancellable = createPublisher(for: repositoryModule.tvShowsRepository.getTrendingShowsNative(categoryList: showCategory))
			.receive(on: DispatchQueue.main)
			.sink(receiveCompletion: { completion in
				self.logger.log(msg:"Received completion: \(completion)")
			}, receiveValue: { [weak self] value in
				
				
				self?.logger.log(msg: "\(value)")
				self?.trendingDataResult = value
				
			})
		
	}
	
	
	func stopObservingTrendingShows() {
		showsCancellable?.cancel()
	}
}
