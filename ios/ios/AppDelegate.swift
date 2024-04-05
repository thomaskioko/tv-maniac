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
    
    @State var themeAppTheme: AppTheme = AppTheme.systemTheme
    
    let applicationComponent: InjectApplicationComponent
    let rootHolder: RootHolder
    let presenterComponent :InjectIosViewPresenterComponent

    override init() {
        rootHolder = RootHolder()
        applicationComponent = InjectApplicationComponent()
        
        presenterComponent = InjectIosViewPresenterComponent(
            componentContext: DefaultComponentContext(
                lifecycle: rootHolder.lifecycle,
                stateKeeper: nil,
                instanceKeeper: nil,
                backHandler: nil
            ),
            applicationComponent: applicationComponent
        )
    }
    
}
