//
//  AsyncImage.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI

struct AsyncImage<Placeholder: View>: View {
	@StateObject private var loader: ImageLoader
	private let placeholder: Placeholder
	private let image: (UIImage) -> Image
	
	init(
		url: URL,
		@ViewBuilder placeholder: () -> Placeholder,
		@ViewBuilder image: @escaping (UIImage) -> Image = Image.init(uiImage:)
	) {
		self.placeholder = placeholder()
		self.image = image
		_loader = StateObject(wrappedValue: ImageLoader(url: url, cache: Environment(\.imageCache).wrappedValue))
	}
	
	var body: some View {
		content
			.onAppear(perform: loader.load)
	}
	
	private var content: some View {
		Group {
			if loader.image != nil {
				image(loader.image!)
			} else {
				placeholder
			}
		}
	}
}
