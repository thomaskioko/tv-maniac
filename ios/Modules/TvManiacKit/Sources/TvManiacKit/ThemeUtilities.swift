//
//  ThemeUtilities.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/20/24.
//

import Foundation
import SwiftUI
import SwiftUIComponents
import TvManiac
import UIKit

public enum DeviceAppTheme: String, CaseIterable, ThemeItem {
    public var id: String {
        rawValue
    }

    case system, light, dark, terminal, autumn, aqua, amber, snow, crimson

    public var overrideTheme: ColorScheme? {
        switch self {
        case .system:
            nil
        case .light, .autumn:
            .light
        case .dark, .terminal, .aqua, .amber, .snow, .crimson:
            .dark
        }
    }

    public var designSystemTheme: TvManiacTheme {
        switch self {
        case .system:
            Self.isSystemDarkMode ? DarkTheme() : LightTheme()
        case .light:
            LightTheme()
        case .dark:
            DarkTheme()
        case .terminal:
            TerminalTheme()
        case .autumn:
            AutumnTheme()
        case .aqua:
            AquaTheme()
        case .amber:
            AmberTheme()
        case .snow:
            SnowTheme()
        case .crimson:
            CrimsonTheme()
        }
    }

    public var displayName: String {
        switch self {
        case .system:
            String(\.settings_theme_system)
        case .light:
            String(\.settings_theme_light)
        case .dark:
            String(\.settings_theme_dark)
        case .terminal:
            String(\.settings_theme_terminal)
        case .autumn:
            String(\.settings_theme_autumn)
        case .aqua:
            String(\.settings_theme_aqua)
        case .amber:
            String(\.settings_theme_amber)
        case .snow:
            String(\.settings_theme_snow)
        case .crimson:
            String(\.settings_theme_crimson)
        }
    }

    public var backgroundColor: Color {
        colorScheme.background
    }

    public var accentColor: Color {
        colorScheme.secondary
    }

    public var onAccentColor: Color {
        colorScheme.onSecondary
    }

    public var isSystemTheme: Bool {
        self == .system
    }

    private var colorScheme: TvManiacColorScheme {
        switch self {
        case .system:
            Self.isSystemDarkMode ? TvManiacColorScheme.dark : TvManiacColorScheme.light
        case .light:
            TvManiacColorScheme.light
        case .dark:
            TvManiacColorScheme.dark
        case .terminal:
            TvManiacColorScheme.terminal
        case .autumn:
            TvManiacColorScheme.autumn
        case .aqua:
            TvManiacColorScheme.aqua
        case .amber:
            TvManiacColorScheme.amber
        case .snow:
            TvManiacColorScheme.snow
        case .crimson:
            TvManiacColorScheme.crimson
        }
    }

    static var isSystemDarkMode: Bool {
        if Thread.isMainThread {
            return UITraitCollection.current.userInterfaceStyle == .dark
        } else {
            var isDark = false
            DispatchQueue.main.sync {
                isDark = UITraitCollection.current.userInterfaceStyle == .dark
            }
            return isDark
        }
    }

    public static var sortedThemes: [DeviceAppTheme] {
        [.system, .light, .dark, .autumn, .aqua, .amber, .snow, .terminal, .crimson]
    }
}

public extension AppTheme {
    func toDeviceAppTheme() -> DeviceAppTheme {
        switch self {
        case .darkTheme:
            .dark
        case .lightTheme:
            .light
        case .terminalTheme:
            .terminal
        case .autumnTheme:
            .autumn
        case .aquaTheme:
            .aqua
        case .amberTheme:
            .amber
        case .snowTheme:
            .snow
        case .crimsonTheme:
            .crimson
        case .systemTheme:
            .system
        default:
            .system
        }
    }
}

public extension ThemeModel {
    func toDeviceAppTheme() -> DeviceAppTheme {
        switch self {
        case .dark:
            .dark
        case .light:
            .light
        case .terminal:
            .terminal
        case .autumn:
            .autumn
        case .aqua:
            .aqua
        case .amber:
            .amber
        case .snow:
            .snow
        case .crimson:
            .crimson
        case .system:
            .system
        default:
            .system
        }
    }
}

public extension DeviceAppTheme {
    func toAppThemeColor() -> Color {
        switch self {
        case .system:
            Self.isSystemDarkMode ? TvManiacColorScheme.dark.background : TvManiacColorScheme.light.background
        case .light:
            TvManiacColorScheme.light.background
        case .dark:
            TvManiacColorScheme.dark.background
        case .terminal:
            TvManiacColorScheme.terminal.background
        case .autumn:
            TvManiacColorScheme.autumn.background
        case .aqua:
            TvManiacColorScheme.aqua.background
        case .amber:
            TvManiacColorScheme.amber.background
        case .snow:
            TvManiacColorScheme.snow.background
        case .crimson:
            TvManiacColorScheme.crimson.background
        }
    }

    func toAppTheme() -> AppTheme {
        switch self {
        case .system:
            .systemTheme
        case .light:
            .lightTheme
        case .dark:
            .darkTheme
        case .terminal:
            .terminalTheme
        case .autumn:
            .autumnTheme
        case .aqua:
            .aquaTheme
        case .amber:
            .amberTheme
        case .snow:
            .snowTheme
        case .crimson:
            .crimsonTheme
        }
    }

    func toThemeModel() -> ThemeModel {
        switch self {
        case .system:
            .system
        case .light:
            .light
        case .dark:
            .dark
        case .terminal:
            .terminal
        case .autumn:
            .autumn
        case .aqua:
            .aqua
        case .amber:
            .amber
        case .snow:
            .snow
        case .crimson:
            .crimson
        }
    }
}
