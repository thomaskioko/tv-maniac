//
//  SettingsViewModel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation


class SettingsViewModel: ObservableObject {

	@Published var appTheme: AppTheme = AppTheme.System

	func startStateMachine() {

	}
}
