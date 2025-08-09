//
//  AppDelegate.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/20/24.
//

import Combine
import SwiftUI
import SwiftUIComponents
import TvManiac
import UIKit

public class AppDelegate: NSObject, UIApplicationDelegate {
    public let lifecycle = LifecycleRegistryKt.LifecycleRegistry()

    private lazy var appComponent = IosApplicationComponent.companion.create()

    public lazy var presenterComponent: IosViewPresenterComponent = appComponent.componentFactory.createComponent(
        componentContext: DefaultComponentContext(lifecycle: lifecycle)
    )

    override public init() {
        super.init()
        LifecycleRegistryExtKt.create(lifecycle)

        initializeApp()

        appComponent.initializers.initialize()
    }

    deinit {
        LifecycleRegistryExtKt.destroy(lifecycle)
    }

    // MARK: - App Initialization

    private func initializeApp() {
        FontRegistration.registerFonts()

        configureImageSystem()

        startImageQualityObserver()

        configureAppearance()

        let quality = SettingsAppStorage.shared.imageQuality
        print("App initialized with image quality: \(quality.rawValue)")
    }

    private func configureImageSystem() {
        let quality = mapToImageConfigurationQuality(SettingsAppStorage.shared.imageQuality)

        ImageConfiguration.configure(quality: quality)

        ImageConfiguration.setupBackgroundMemoryManagement()
    }

    private func mapToImageConfigurationQuality(_ quality: SwiftImageQuality) -> ImageConfiguration.Quality {
        switch quality {
        case .high:
            .high
        case .medium:
            .medium
        case .low:
            .low
        }
    }

    private func startImageQualityObserver() {
        SettingsAppStorage.shared.objectWillChange
            .sink { [weak self] _ in
                self?.configureImageSystem()
            }
            .store(in: &cancellables)
    }

    private var cancellables = Set<AnyCancellable>()

    private func configureAppearance() {
        // Configure navigation bar appearance
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }
}
