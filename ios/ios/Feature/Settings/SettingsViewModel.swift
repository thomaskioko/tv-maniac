//
//  SettingsViewModel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import TvManiac

class SettingsViewModel: ObservableObject {

    private var stateMachine: SettingsStateMachineWrapper = ApplicationComponentKt.settingsStateMachine()
	@Published private (set) var settingsState: SettingsState = SettingsContent.companion.EMPTY
	@Published var appTheme: AppTheme = AppTheme.System

	func startStateMachine() {
		stateMachine.start(stateChangeListener: { (state: SettingsState) -> Void in
			self.settingsState = state
			
			if let themeState = state as? SettingsContent {
				self.appTheme = toAppTheme(theme: themeState.theme)
			}
		})
	}

	func dispatchAction(action: SettingsActions){
		stateMachine.dispatch(action: action)
	}
    
    func cancel() {
        stateMachine.cancel()
    }
}
