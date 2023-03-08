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

	@LazyKoin private var stateMachine: SettingsStateMachineWrapper
	@Published private (set) var settingsState: SettingsState
	@Published var appTheme: AppTheme = AppTheme.System

	init(settingsState: SettingsState){
		self.settingsState = settingsState
	}

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
