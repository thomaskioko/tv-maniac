//
//  ScreenRegistry+Typed.swift
//  TvManiacKit
//
//  Typed convenience wrappers over the base `registerScreen` / `registerSheet` closures. Callers
//  pass the presenter type and a view builder; the extension handles the `ScreenDestination` /
//  `SheetDestination` cast, the `AnyView` wrap, and the `ObjectIdentifier`-based id that keeps
//  screen state stable across stack updates.
//

import SwiftUI
import TvManiac

public extension ScreenRegistry {
    func registerScreen<P: AnyObject>(
        for _: P.Type,
        @ViewBuilder builder: @escaping (P) -> some View
    ) {
        registerScreen { child in
            guard let screen = child as? ScreenDestination<AnyObject>,
                  let presenter = screen.presenter as? P else { return nil }
            return AnyView(
                builder(presenter)
                    .id(ObjectIdentifier(child as AnyObject))
            )
        }
    }

    func registerSheet<P: AnyObject>(
        for _: P.Type,
        @ViewBuilder builder: @escaping (P) -> some View,
        dismiss: @escaping (P) -> Void
    ) {
        registerSheet(
            build: { child in
                guard let sheet = child as? SheetDestination<AnyObject>,
                      let presenter = sheet.presenter as? P else { return nil }
                return AnyView(builder(presenter))
            },
            dismiss: { child in
                guard let sheet = child as? SheetDestination<AnyObject>,
                      let presenter = sheet.presenter as? P else { return false }
                dismiss(presenter)
                return true
            }
        )
    }
}
