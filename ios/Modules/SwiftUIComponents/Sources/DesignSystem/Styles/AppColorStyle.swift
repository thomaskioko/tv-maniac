import SwiftUI

public struct AppColorStyle: ShapeStyle {
    private let keyPath: KeyPath<TvManiacColorScheme, Color>

    public init(_ keyPath: KeyPath<TvManiacColorScheme, Color>) {
        self.keyPath = keyPath
    }

    public func resolve(in environment: EnvironmentValues) -> Color {
        environment.appTheme.colors[keyPath: keyPath]
    }
}

public extension ShapeStyle where Self == AppColorStyle {
    static var appPrimary: AppColorStyle {
        .init(\.primary)
    }

    static var appPrimaryContainer: AppColorStyle {
        .init(\.primaryContainer)
    }

    static var appOnPrimary: AppColorStyle {
        .init(\.onPrimary)
    }

    static var appSecondary: AppColorStyle {
        .init(\.secondary)
    }

    static var appOnSecondary: AppColorStyle {
        .init(\.onSecondary)
    }

    static var appError: AppColorStyle {
        .init(\.error)
    }

    static var appOnError: AppColorStyle {
        .init(\.onError)
    }

    static var appBackground: AppColorStyle {
        .init(\.background)
    }

    static var appOnBackground: AppColorStyle {
        .init(\.onBackground)
    }

    static var appSurface: AppColorStyle {
        .init(\.surface)
    }

    static var appOnSurface: AppColorStyle {
        .init(\.onSurface)
    }

    static var appSurfaceVariant: AppColorStyle {
        .init(\.surfaceVariant)
    }

    static var appOnSurfaceVariant: AppColorStyle {
        .init(\.onSurfaceVariant)
    }

    static var appOutline: AppColorStyle {
        .init(\.outline)
    }

    static var appAccent: AppColorStyle {
        .init(\.accent)
    }

    static var appOnAccent: AppColorStyle {
        .init(\.onAccent)
    }

    static var appButtonBackground: AppColorStyle {
        .init(\.buttonBackground)
    }

    static var appOnButtonBackground: AppColorStyle {
        .init(\.onButtonBackground)
    }

    static var appSuccess: AppColorStyle {
        .init(\.success)
    }

    static var appGrey: AppColorStyle {
        .init(\.grey)
    }
}
