//
//  ScreenRegistry.swift
//  TvManiacKit
//
//  Platform-side renderer registry for root stack screens and modal sheets. Mirrors the Android
//  `Set<ScreenContent>` / `Set<SheetContent>` multibinding pattern. Consumers register their
//  presenter to view mappings at app startup via `registerScreen` / `registerSheet`, and the
//  `RootNavigationView` queries the registry to render the active child without any central
//  `switch presenter` block.
//

import SwiftUI
import TvManiac

public final class ScreenRegistry {
    public typealias ScreenBuilder = (Any) -> AnyView?
    public typealias SheetBuilder = (Any) -> AnyView?
    public typealias SheetDismiss = (Any) -> Bool

    private var screenBuilders: [ScreenBuilder] = []
    private var sheetBuilders: [(build: SheetBuilder, dismiss: SheetDismiss)] = []

    public init() {}

    public func registerScreen(_ builder: @escaping ScreenBuilder) {
        screenBuilders.append(builder)
    }

    public func registerSheet(build: @escaping SheetBuilder, dismiss: @escaping SheetDismiss) {
        sheetBuilders.append((build, dismiss))
    }

    @ViewBuilder
    public func view(for child: Any) -> some View {
        if let built = screenBuilders.lazy.compactMap({ $0(child) }).first {
            built
        } else {
            EmptyView()
        }
    }

    @ViewBuilder
    public func sheet(for child: Any) -> some View {
        if let built = sheetBuilders.lazy.compactMap({ $0.build(child) }).first {
            built
        } else {
            EmptyView()
        }
    }

    public func dismissSheet(child: Any) {
        for entry in sheetBuilders {
            if entry.dismiss(child) { return }
        }
    }
}
