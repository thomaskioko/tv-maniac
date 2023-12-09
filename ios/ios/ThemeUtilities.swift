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
import SwiftUI

enum DeveiceAppTheme: Int, CaseIterable {
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
  
}

func toTheme(appTheme: DeveiceAppTheme) -> AppTheme {
    switch appTheme {
    case .System:
        return AppTheme.systemTheme
    case .Light:
        return AppTheme.lightTheme
    case .Dark:
        return AppTheme.darkTheme
    }
}

func toAppTheme(theme: AppTheme) -> DeveiceAppTheme {
    switch theme {
    case AppTheme.darkTheme:
        return DeveiceAppTheme.Dark
    case AppTheme.lightTheme:
        return DeveiceAppTheme.Light
    default:
        return DeveiceAppTheme.System
    }
}
