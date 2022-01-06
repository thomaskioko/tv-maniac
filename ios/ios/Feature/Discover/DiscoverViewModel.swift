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

final class DiscoverViewModel: BaseViewModel, ObservableObject {

	@Published var state: DiscoverShowState = DiscoverShowState.companion.Empty
	
    private let logger = Logger(className: "DiscoverViewModel")

    private var showsCancellable: AnyCancellable?

	private let interactor: ObserveDiscoverShowsInteractor
    
	init(interactor: ObserveDiscoverShowsInteractor) {
		self.interactor = interactor
	}

    func startObservingDiscoverShows() {

		interactor.execute(self, args: nil) {
            $0.onNext { result in
				self.updateState(showResult: result!)
            }

            $0.onError { error in
                self.logger.log(msg: "\(error)")
            }
        }
    }


    private func updateState(
            showResult: DiscoverShowResult
    ) {

		state = DiscoverShowState(
			isLoading: showResult.trendingShows.isLoading,
			showData: showResult
		)

    }


    func stopObservingTrendingShows() {
        showsCancellable?.cancel()
    }

}
