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
    
    let applicationComponent: ApplicationComponent
    let rootHolder: RootHolder
    let presenterComponent: IosViewPresenterComponent

    override init() {
        rootHolder = RootHolder()
        applicationComponent = ApplicationComponent.companion.create()
        
        presenterComponent = IosViewPresenterComponent.companion.create(
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
