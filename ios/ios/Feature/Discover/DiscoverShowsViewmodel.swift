//
//  DiscoverShowsViewmodel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 06.11.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import TvManiac


class DiscoverShowsViewModel: ObservableObject {
    @LazyKoin private var stateMachine: DiscoverStateMachineWrapper
    @Published private(set) var showState: ShowsState

    init(showState: ShowsState) {
        self.showState = showState
    }

    func startStateMachine() {
        stateMachine.start(stateChangeListener: { (state: ShowsState) -> Void in
            self.showState = state
        })
    }
    
    func cancel() {
        stateMachine.cancel()
    }
}
