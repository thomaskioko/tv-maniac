//
//  DiscoverVIewModel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 31.10.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation
import Combine
import TvManiac

final class DiscoverViewModel : BaseViewModel, ObservableObject {

	private let logger = Logger(className: "DiscoverViewModel")
	
	private var showsCancellable: AnyCancellable?
	
	private let networkModule: NetworkModule
	private let databaseModule: DatabaseModule
	private let repositoryModule: RepositoryModule
	
	let interactor: GetDiscoverShowListInteractor
	
	@Published var state : DiscoverShowState = DiscoverShowState.companion.Empty
	
	
	init(
			networkModule: NetworkModule,
			databaseModule: DatabaseModule,
			interactor: GetDiscoverShowListInteractor
	){
		self.networkModule = networkModule
		self.databaseModule = databaseModule
		repositoryModule = RepositoryModule(
			networkModule: self.networkModule,
			databaseModule: self.databaseModule
		)
		self.interactor = interactor
		
	}
	
	func startObservingDiscoverShows(){
		let showCategory: NSArray = [ShowCategory.featured, ShowCategory.trending, ShowCategory.topRated, ShowCategory.popular]

		interactor.execute(self, args: showCategory) {
			$0.onStart {
				self.updateState(
						isLoading: true,
						list: [TrendingShowData]()
				)
			}

			$0.onNext { list in
				self.updateState(
						isLoading: false,
						list: (list as! [TrendingShowData])
				)
			}

			$0.onError { error in
				self.logger.log(msg: "\(error)")

				self.updateState(
						isLoading: false,
						list: [TrendingShowData]()
				)
			}
		}
	}
	
	private func updateState(
		isLoading: Bool,
		list: [TrendingShowData]
	){
		
		state = DiscoverShowState(
			isLoading: isLoading,
			list: list
		)
	
	}
	
	
	func stopObservingTrendingShows() {
		showsCancellable?.cancel()
	}
}
