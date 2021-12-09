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
	
	let getDiscoverShowListInteractor: GetDiscoverShowListInteractor
	
	@Published var state : DiscoverShowState = DiscoverShowState.companion.Empty
	
	
	init(
		networkModule: NetworkModule,
		databaseModule: DatabaseModule,
		getDiscoverShowListInteractor: GetDiscoverShowListInteractor
	){
		self.networkModule = networkModule
		self.databaseModule = databaseModule
		self.repositoryModule = RepositoryModule(
			networkModule: self.networkModule,
			databaseModule: self.databaseModule
		)
		self.getDiscoverShowListInteractor = getDiscoverShowListInteractor
		
	}
	
	
	func startObservingTrendingShows(){
		let showCategory: NSArray = [ShowCategory.featured, ShowCategory.trending, ShowCategory.topRated, ShowCategory.popular]
		
		
		showsCancellable = createPublisher(for: getDiscoverShowListInteractor.invokeNative(params: showCategory))
			.receive(on: DispatchQueue.main)
			.sink(receiveCompletion: { completion in
				self.logger.log(msg:"Received completion: \(completion)")
			}, receiveValue: { [weak self] value in
				
				switch value {
				case is DomainResultStateError<NSArray>:
					self?.updateState(
						isLoading: false,
						list: [TrendingShowData]()
					)
				case is DomainResultStateLoading<NSArray>:
					self?.updateState(
						isLoading: true,
						list: [TrendingShowData]()
					)
				case is DomainResultStateSuccess<NSArray>:
					self?.updateState(
						isLoading: false,
						list: ((value as! DomainResultStateSuccess).data as! [TrendingShowData])
					)
				default:
					self?.logger.log(msg: "Handle unknown state!")
				}
			})
		
	}
	
	private func updateState(
		isLoading: Bool,
		list: [TrendingShowData]
	){
		
		self.state = DiscoverShowState(
			isLoading: isLoading,
			list: list
		)
	
	}
	
	
	func stopObservingTrendingShows() {
		showsCancellable?.cancel()
	}
}
