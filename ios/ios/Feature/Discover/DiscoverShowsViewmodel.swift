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
    @Published private(set) var showState: DiscoverState = Loading()
    @Published var toast: Toast? = nil
    
    func startStateMachine() {
        stateMachine.start(stateChangeListener: { (state: DiscoverState) -> Void in
            self.showState = state
            if(state is DataLoaded){
                let dataLoaded = state as! DataLoaded
                if(!dataLoaded.isContentEmpty && dataLoaded.errorMessage != nil){
                    self.toast = Toast(type: .error, title: "Error", message: dataLoaded.errorMessage!)
                } 
            }
        })
    }
}
