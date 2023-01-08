//
// Created by Thomas Kioko on 24.11.22.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import TvManiac

class ShowDetailsViewModel: ObservableObject {
    @LazyKoin private var stateMachine: ShowDetailsStateMachineWrapper
    @Published private(set) var detailState: ShowDetailsState

    init(detailState: ShowDetailsState) {
        self.detailState = detailState
    }

    func startStateMachine(action: ShowDetailsAction) {
        stateMachine.start(stateChangeListener: { (state: ShowDetailsState) -> Void in
            self.detailState = state
        })
        stateMachine.dispatch(action: action)
    }

    func dispatchAction(action: ShowDetailsAction){
        stateMachine.dispatch(action: action)
    }

    func dismiss(){
        stateMachine.cancel()
    }

}
