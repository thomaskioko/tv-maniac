//
//  ImageCache.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import UIKit

protocol ImageCache {
	subscript(_ url: URL) -> UIImage? { get set }
}

struct TemporaryImageCache: ImageCache {
	private let cache: NSCache<NSURL, UIImage> = {
		let cache = NSCache<NSURL, UIImage>()
		cache.countLimit = 100 // 100 items
		cache.totalCostLimit = 1024 * 1024 * 100 // 100 MB
		return cache
	}()
	
	subscript(_ key: URL) -> UIImage? {
		get { cache.object(forKey: key as NSURL) }
		set { newValue == nil ? cache.removeObject(forKey: key as NSURL) : cache.setObject(newValue!, forKey: key as NSURL) }
	}
}
