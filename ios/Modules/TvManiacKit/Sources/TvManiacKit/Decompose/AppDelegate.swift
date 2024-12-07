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
  public let lifecycle = LifecycleRegistryKt.LifecycleRegistry()

  private lazy var appComponent = IosApplicationComponent.companion.create()

  public lazy var presenterComponent: IosViewPresenterComponent = appComponent.componentFactory.createComponent(
    componentContext: DefaultComponentContext(lifecycle: lifecycle)
  )

  public override init() {
    super.init()
    LifecycleRegistryExtKt.create(lifecycle)
  }

  deinit {
    LifecycleRegistryExtKt.destroy(lifecycle)
  }
}

