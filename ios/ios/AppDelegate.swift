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
  private lazy var appComponent = IosApplicationComponent.companion.create()

  lazy var presenterComponent: IosViewPresenterComponent = appComponent.componentFactory.createComponent(
    componentContext: DefaultComponentContext(
      lifecycle: ApplicationLifecycle(),
      stateKeeper: nil,
      instanceKeeper: nil,
      backHandler: nil
    )
  )
}
