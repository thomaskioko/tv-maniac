//
//  SettingsUtil.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import UIKit

enum AppTheme: Int, CaseIterable {
	case Light = 0
	case Dark = 1
	case System = 2

	func getTheme(value: Int) -> AppTheme {
		switch value {
		case 1:
			return AppTheme.Dark
		case 0:
			return AppTheme.Light
		default:
			return AppTheme.System
		}
	}

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

}
