//
//  AppDelegate.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/20/24.
//

import SwiftUI
import TvManiac
import UIKit

public class AppDelegate: NSObject, UIApplicationDelegate, ObservableObject {
    public lazy var appComponent = IosApplicationComponent.companion.create()

    public lazy var traktAuthRepository = appComponent.traktAuthRepository
    public lazy var logger = appComponent.logger
    public lazy var traktAuthManager = appComponent.traktAuthManager

    override public init() {
        super.init()
        initializeApp()
        appComponent.initializers.initialize()
    }

    public func setupAuthBridge(authCallback: @escaping () -> Void) {
        traktAuthManager.setAuthCallback(callback: authCallback)
    }

    // MARK: - App Initialization

    private func initializeApp() {
        ImageSystemManager.shared.initialize()
    }
}
