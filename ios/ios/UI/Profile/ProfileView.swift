//
//  ProfileView.swift
//  tv-maniac
//
//  Created by Kioko on 03/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiacKit

struct ProfileView: View {
    @StateObject private var authBridge: ObservableTraktAuth
    @State var isPresented = false

    init(authBridge: ObservableTraktAuth) {
        _authBridge = StateObject(wrappedValue: authBridge)
    }

    var body: some View {
        NavigationView {
            if !authBridge.isAuthenticated {
                EmptyView()
                    .toolbar {
                        ToolbarItem(placement: .navigationBarTrailing) {
                            Button(
                                action: { isPresented = true },
                                label: {
                                    Image(systemName: "gearshape")
                                        .resizable()
                                        .aspectRatio(contentMode: .fit)
                                        .foregroundColor(.grey200)
                                        .frame(width: 24, height: 24)
                                }
                            )
                            .sheet(
                                isPresented: $isPresented,
                                content: {
                                    // SettingsUIView()
                                }
                            )
                        }
                    }
            }
        }
    }
}
