//
//  ColorExtension.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

extension UINavigationController {
	// Remove back button text
	open override func viewWillLayoutSubviews() {
		navigationBar.topItem?.backBarButtonItem = UIBarButtonItem(title: "", style: .plain, target: nil, action: nil)
	}
}
