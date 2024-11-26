//
//  AppDelegate.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/20/24.
//

import SwiftUI
import UIKit
import TvManiac

public class AppDelegate: NSObject, UIApplicationDelegate {
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

