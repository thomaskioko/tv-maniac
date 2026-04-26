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

private typealias KmpTheme = TvManiac.Theme

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

public extension KmpTheme {
    func toDeviceAppTheme() -> DeviceAppTheme {
        if self == KmpTheme.darkTheme { return .dark }
        if self == KmpTheme.lightTheme { return .light }
        if self == KmpTheme.terminalTheme { return .terminal }
        if self == KmpTheme.autumnTheme { return .autumn }
        if self == KmpTheme.aquaTheme { return .aqua }
        if self == KmpTheme.amberTheme { return .amber }
        if self == KmpTheme.snowTheme { return .snow }
        if self == KmpTheme.crimsonTheme { return .crimson }
        return .system
    }
}

public extension ThemeModel {
    func toDeviceAppTheme() -> DeviceAppTheme {
        if self == ThemeModel.dark { return .dark }
        if self == ThemeModel.light { return .light }
        if self == ThemeModel.terminal { return .terminal }
        if self == ThemeModel.autumn { return .autumn }
        if self == ThemeModel.aqua { return .aqua }
        if self == ThemeModel.amber { return .amber }
        if self == ThemeModel.snow { return .snow }
        if self == ThemeModel.crimson { return .crimson }
        return .system
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

    func toTheme() -> TvManiac.Theme {
        switch self {
        case .system: KmpTheme.systemTheme
        case .light: KmpTheme.lightTheme
        case .dark: KmpTheme.darkTheme
        case .terminal: KmpTheme.terminalTheme
        case .autumn: KmpTheme.autumnTheme
        case .aqua: KmpTheme.aquaTheme
        case .amber: KmpTheme.amberTheme
        case .snow: KmpTheme.snowTheme
        case .crimson: KmpTheme.crimsonTheme
        }
    }

    func toThemeModel() -> ThemeModel {
        switch self {
        case .system: ThemeModel.system
        case .light: ThemeModel.light
        case .dark: ThemeModel.dark
        case .terminal: ThemeModel.terminal
        case .autumn: ThemeModel.autumn
        case .aqua: ThemeModel.aqua
        case .amber: ThemeModel.amber
        case .snow: ThemeModel.snow
        case .crimson: ThemeModel.crimson
        }
    }
}
