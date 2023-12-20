//
//  SettingsUIView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import TvManiac

struct SettingsView: View {
    
    private let presenter: SettingsPresenter
    @StateValue private var uiState: SettingsState
    @Environment(\.openURL) var openURL
    @Environment(\.presentationMode) var presentationMode
    
    @State private var theme: DeveiceAppTheme = DeveiceAppTheme.System
    @State private var showingAlert: Bool = false
    @State private var openInYouTube: Bool = false
    @ObservedObject private var model = TraktAuthViewModel()
    
    
    init(presenter: SettingsPresenter){
        self.presenter = presenter
        _uiState = StateValue(presenter.state)
        theme = toAppTheme(theme: uiState.appTheme)
    }
    
    var body: some View {
        NavigationStack {
            Form {
                
                Section(header: Text("App Theme").bodyMediumFont(size: 16)) {
                    
                    Picker(
                        selection: $theme,
                        label: Text("Change Theme")
                            .bodyMediumFont(size: 16),
                        content: {
                            ForEach(DeveiceAppTheme.allCases, id: \.self) { theme in
                                
                                Text(theme.getName())
                                    .tag(theme.rawValue)
                                
                            }
                        })
                    .pickerStyle(.segmented)
                    .padding(.vertical, 6)
                    .onChange(of: theme) { theme in
                        presenter.dispatch(action: ThemeSelected(appTheme: toTheme(appTheme: theme)))
                    }
                }
                
                Section(header: Text("Trailer Settings").bodyMediumFont(size: 16)) {
                    Toggle(isOn: $openInYouTube) {
                        Text("Open Trailers in Youtube App")
                    }
                }
                
                Section(header: Text("Trakt Account").bodyMediumFont(size: 16)) {
                    
                    SettingsItem(
                        image: "person.fill",
                        title: "Connect to Trakt",
                        description: "Trakt is a platform that does many things, but primarily keeps track of TV shows and movies you watch."
                    ) {
                        showingAlert = !uiState.showTraktDialog
                    }
                    .alert(isPresented: $showingAlert) {
                        Alert(
                            title: Text("Trakt Coming Soon"),
                            message: Text("Trakt is a platform that does many things, but primarily keeps track of TV shows and movies you watch."),
                            primaryButton: .default(Text("Login")) {
                                model.initiateAuthorization()
                                
                            },
                            secondaryButton: .destructive(Text("Cancel"))
                        )
                    }
                }
                
                Section(header: Text("Info").bodyMediumFont(size: 16)) {
                    
                    SettingsItem(
                        image: "info.circle.fill",
                        title: "About TvManiac",
                        description: "Tv-Maniac is a Multiplatform app (Android &amp; iOS) for viewing TV Shows from TMDB."
                    ) {
                        openURL(URL(string: "https://github.com/c0de-wizard/tv-maniac")!)
                    }
                }
            }
                .navigationTitle("Settings")
                .navigationBarTitleDisplayMode(.large)
                .onAppear {
                    theme = toAppTheme(theme: uiState.appTheme)
                }
        }
    }
    
}

struct SettingsItem: View {
    let image: String
    let title: String
    let description: String
    let onClick: () -> Void
    
    
    var body: some View {
        HStack(alignment: .center) {
            Image(systemName: image)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(Color.accent)
                .frame(width: 20, height: 24, alignment: .leading)
                .padding(.leading, 8)
                .padding(.trailing, 8)
            
            VStack(alignment: .leading) {
                Text(title)
                    .bodyMediumFont(size: 16)
                
                Text(description)
                    .bodyFont(size: 16)
                    .padding(.top, 1.5)
            }
        }
        .onTapGesture(perform: onClick)
    }
}
