//
//  AppDelegate.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/20/24.
//

import SwiftUI
import UIKit
import TvManiac
import SDWebImage

public class AppDelegate: NSObject, UIApplicationDelegate {
  public let lifecycle = LifecycleRegistryKt.LifecycleRegistry()

  private lazy var appComponent = IosApplicationComponent.companion.create()

  public lazy var presenterComponent: IosViewPresenterComponent = appComponent.componentFactory.createComponent(
    componentContext: DefaultComponentContext(lifecycle: lifecycle)
  )

  public override init() {
    super.init()
    LifecycleRegistryExtKt.create(lifecycle)
    configureSDWebImage()
  }

  deinit {
    LifecycleRegistryExtKt.destroy(lifecycle)
  }

  private func configureSDWebImage() {
    // Configure cache limits
    SDImageCache.shared.config.maxMemoryCost = 1024 * 1024 * 20 // 20MB memory cache
    SDImageCache.shared.config.maxDiskAge = 3600 * 24 * 7 // 1 week disk cache
    SDImageCache.shared.config.maxDiskSize = 1024 * 1024 * 100 // 100MB disk cache
    
    // Use memory mapping for disk cache reading
    SDImageCache.shared.config.diskCacheReadingOptions = .mappedIfSafe
    
    // Configure global options processor
    SDWebImageManager.shared.optionsProcessor = SDWebImageOptionsProcessor { url, options, context in
      var mutableOptions = options
      mutableOptions.insert(.scaleDownLargeImages)
      
      var mutableContext = context ?? [:]
      mutableContext[.imageForceDecodePolicy] = SDImageForceDecodePolicy.never.rawValue
      
      return SDWebImageOptionsResult(options: mutableOptions, context: mutableContext)
    }
    
    // Configure download operation settings
    SDWebImageDownloader.shared.config.downloadTimeout = 15.0 // 15 seconds timeout
    SDWebImageDownloader.shared.config.maxConcurrentDownloads = 6 // Limit concurrent downloads
    
    // Clear memory cache when app enters background
    NotificationCenter.default.addObserver(
      forName: UIApplication.didEnterBackgroundNotification,
      object: nil,
      queue: .main
    ) { _ in
      SDImageCache.shared.clearMemory()
    }
  }
}

