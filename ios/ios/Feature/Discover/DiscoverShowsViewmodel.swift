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
    private let stateMachine: DiscoverStateMachineWrapper = ApplicationComponentKt.discoverStateMachine()
    @Published private(set) var showState: ShowsState = Loading()

    func startStateMachine() {
        stateMachine.start(stateChangeListener: { (state: ShowsState) -> Void in
            self.showState = state
        })
    }
}
