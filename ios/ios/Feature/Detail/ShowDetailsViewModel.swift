//
// Created by Thomas Kioko on 24.11.22.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import TvManiac

class ShowDetailsViewModel: ObservableObject {
    private let stateMachine: ShowDetailsStateMachineWrapper = ApplicationComponentKt.showDetailsStateMachine()
    
    @Published private(set) var detailState: ShowDetailsState = ShowDetailsLoaded.companion.EMPTY_DETAIL_STATE
    
    func startStateMachine(showId: Int64, action: ShowDetailsAction) {
        stateMachine.start(showId: showId, stateChangeListener: { (state: ShowDetailsState) -> Void in
            self.detailState = state
        })
        stateMachine.dispatch(showId: showId, action: action)
    }
    
    func dispatchAction(showId: Int64, action: ShowDetailsAction){
        stateMachine.dispatch(showId: showId, action: action)
    }
    
}
