//
// Created by Thomas Kioko on 24.11.22.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import TvManiac

class ShowDetailsViewModel: ObservableObject {
    private let stateMachine: ShowDetailsStateMachineWrapper = ApplicationComponentKt.showDetailsStateMachine()
    @Published private(set) var detailState: ShowDetailsState = ShowDetailsStateLoading()

    func startStateMachine(action: ShowDetailsAction) {
        stateMachine.start(stateChangeListener: { (state: ShowDetailsState) -> Void in
            self.detailState = state
        })
        stateMachine.dispatch(action: action)
    }

    func dispatchAction(action: ShowDetailsAction){
        stateMachine.dispatch(action: action)
    }

}
