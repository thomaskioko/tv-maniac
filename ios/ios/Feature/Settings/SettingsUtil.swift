//
//  SettingsUtil.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import TvManiac
import UIKit

enum AppTheme: Int, CaseIterable {
	case Light = 0
	case Dark = 1
	case System = 2

	func getName() -> String {
		switch self {
		case .System:
			return "System Theme"
		case .Light:
			return "Light Theme"
		case .Dark:
			return "Dark Theme"
		}
	}

	func toTheme() -> Theme {
		switch self {
		case .System:
			return Theme.system
		case .Light:
			return Theme.light
		case .Dark:
			return Theme.dark
		}
	}
}

func toAppTheme(theme: Theme) -> AppTheme {
	switch theme {
	case Theme.dark:
		return AppTheme.Dark
	case Theme.light:
		return AppTheme.Light
	default:
		return AppTheme.System
	}
}
