//
//  AppDelegate.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 03.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import UIKit
import TvManiac

class AppDelegate: NSObject, UIApplicationDelegate {
    lazy var presenterComponent: IosViewPresenterComponent = IosViewPresenterComponent.companion.create(
        componentContext: DefaultComponentContext(
            lifecycle: ApplicationLifecycle(),
            stateKeeper: nil,
            instanceKeeper: nil,
            backHandler: nil
        ),
        applicationComponent: ApplicationComponent.companion.create()
    )
}
