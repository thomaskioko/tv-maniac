//
//  ThemeUtilities.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/20/24.
//

import Foundation
import SwiftUI
import TvManiac

public enum DeveiceAppTheme: String, CaseIterable {
  var id: String { rawValue }
  case system, light, dark
  public var overrideTheme: ColorScheme? {
    switch self {
    case .system:
      return nil
    case .light:
      return .light
    case .dark:
      return .dark
    }
  }

  public var localizableName: String {
    switch self {
    case .system:
      return NSLocalizedString("System", comment: "")
    case .light:
      return NSLocalizedString("Light", comment: "")
    case .dark:
      return NSLocalizedString("Dark", comment: "")
    }
  }
}

public func toTheme(appTheme: DeveiceAppTheme) -> AppTheme {
  switch appTheme {
  case .system:
    return AppTheme.systemTheme
  case .light:
    return AppTheme.lightTheme
  case .dark:
    return AppTheme.darkTheme
  }
}

public extension AppTheme {
  func toDeveiceAppTheme() -> DeveiceAppTheme {
    switch self {
    case .darkTheme:
      return .dark
    case .lightTheme:
      return .light
    default:
      return .system
    }
  }
}

public extension DeveiceAppTheme {
  func toAppThemeColor() -> Color {
    switch self {
    case .dark:
      return Color(.backgroundDark)
    case .light:
      return Color(.systemGroupedBackground)
    default:
      return .background
    }
  }
}
