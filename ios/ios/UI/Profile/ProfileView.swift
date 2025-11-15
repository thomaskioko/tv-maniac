//
//  ProfileView.swift
//  tv-maniac
//
//  Created by Kioko on 03/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct ProfileView: View {
    @Binding var isAuthenticated: Bool
    @State var isPresented = false

    var body: some View {
        NavigationView {
            if !isAuthenticated {
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
