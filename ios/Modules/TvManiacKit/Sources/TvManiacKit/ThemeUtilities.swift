//
//  ThemeUtilities.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/20/24.
//

import Foundation
import TvManiac
import SwiftUI

public enum DeveiceAppTheme: Int, CaseIterable {
  case Light = 0
  case Dark = 1
  case System = 2

  public func getName() -> String {
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

public func toTheme(appTheme: DeveiceAppTheme) -> AppTheme {
  switch appTheme {
    case .System:
      return AppTheme.systemTheme
    case .Light:
      return AppTheme.lightTheme
    case .Dark:
      return AppTheme.darkTheme
  }
}

public extension AppTheme {
  func toDeveiceAppTheme() -> DeveiceAppTheme {
    switch self {
      case .darkTheme:
        return .Dark
      case .lightTheme:
        return .Light
      default:
        return .System
    }
  }
}
