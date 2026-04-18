//
//  ScreenRegistryBootstrap.swift
//  tv-maniac
//
//  Registers every feature presenter->view mapping into the `ScreenRegistry` at app startup.
//  Equivalent to the Android `Set<ScreenContent>` / `Set<SheetContent>` Metro multibindings:
//  adding a new screen means adding one line here. `RootNavigationView` itself does not change.
//

import SwiftUI
import TvManiac
import TvManiacKit

enum ScreenRegistryBootstrap {
    static func makeRegistry() -> ScreenRegistry {
        let registry = ScreenRegistry()
        registerScreens(into: registry)
        registerSheets(into: registry)
        return registry
    }

    private static func registerScreens(into registry: ScreenRegistry) {
        registry.registerScreen(for: HomePresenter.self) { TabBarView(presenter: $0) }
        registry.registerScreen(for: ShowDetailsPresenter.self) { ShowDetailsView(presenter: $0) }
        registry.registerScreen(for: SeasonDetailsPresenter.self) { SeasonDetailsView(presenter: $0) }
        registry.registerScreen(for: SearchShowsPresenter.self) { SearchTab(presenter: $0) }
        registry.registerScreen(for: SettingsPresenter.self) { SettingsView(presenter: $0) }
        registry.registerScreen(for: DebugPresenter.self) { DebugMenuView(presenter: $0) }
        registry.registerScreen(for: MoreShowsPresenter.self) { MoreShowsView(presenter: $0) }
    }

    private static func registerSheets(into registry: ScreenRegistry) {
        registry.registerSheet(
            for: EpisodeSheetPresenter.self,
            builder: { EpisodeDetailSheetView(presenter: $0) },
            dismiss: { $0.dispatch(action: EpisodeSheetActionDismiss()) }
        )
    }
}
