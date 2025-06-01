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
    var id: String {
        rawValue
    }

    case system, light, dark
    public var overrideTheme: ColorScheme? {
        switch self {
        case .system:
            nil
        case .light:
            .light
        case .dark:
            .dark
        }
    }

    public var localizableName: String {
        switch self {
        case .system:
            NSLocalizedString("System", comment: "")
        case .light:
            NSLocalizedString("Light", comment: "")
        case .dark:
            NSLocalizedString("Dark", comment: "")
        }
    }
}

public extension AppTheme {
    func toDeveiceAppTheme() -> DeveiceAppTheme {
        switch self {
        case .darkTheme:
            .dark
        case .lightTheme:
            .light
        default:
            .system
        }
    }
}

public extension DeveiceAppTheme {
    func toAppThemeColor() -> Color {
        switch self {
        case .dark:
            Color(.backgroundDark)
        case .light:
            Color(.systemGroupedBackground)
        default:
            .background
        }
    }
}
