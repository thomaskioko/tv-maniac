//
//  EnvironmentValues+ImageCache.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI

struct ImageCacheKey: EnvironmentKey {
	static let defaultValue: ImageCache = TemporaryImageCache()
}

extension EnvironmentValues {
	var imageCache: ImageCache {
		get { self[ImageCacheKey.self] }
		set { self[ImageCacheKey.self] = newValue }
	}
}
