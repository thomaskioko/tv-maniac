import DesignSystem
import SwiftUI

public enum WidgetTheme {
    public static func named(_ name: String?, colorScheme: ColorScheme) -> TvManiacTheme {
        switch name {
        case "LIGHT_THEME": LightTheme()
        case "DARK_THEME": DarkTheme()
        case "TERMINAL_THEME": TerminalTheme()
        case "AUTUMN_THEME": AutumnTheme()
        case "AQUA_THEME": AquaTheme()
        case "AMBER_THEME": AmberTheme()
        case "SNOW_THEME": SnowTheme()
        case "CRIMSON_THEME": CrimsonTheme()
        default: colorScheme == .dark ? DarkTheme() : LightTheme()
        }
    }

    public static func prefersDarkAppearance(_ name: String?) -> Bool? {
        switch name {
        case "LIGHT_THEME", "AUTUMN_THEME": false
        case "DARK_THEME", "TERMINAL_THEME", "AQUA_THEME", "AMBER_THEME", "SNOW_THEME", "CRIMSON_THEME": true
        default: nil
        }
    }
}
