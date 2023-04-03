    //
    //  SettingsUIView.swift
    //  tv-maniac
    //
    //  Created by Thomas Kioko on 07.12.22.
    //  Copyright © 2022 orgName. All rights reserved.
    //

import SwiftUI
import TvManiac

struct SettingsUIView: View {
    
    @ObservedObject var viewModel: SettingsViewModel = SettingsViewModel(settingsState: SettingsContent.companion.EMPTY)
    @ObservedObject private var model = TraktAuthViewModel()
    
    @Environment(\.openURL) var openURL
    @Environment(\.presentationMode) var presentationMode
    
    @SwiftUI.State private var showingAlert: Bool = false
    
    var body: some View {
        NavigationView {
            SettingsForm()
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button (
                        action: {
                            self.presentationMode.wrappedValue.dismiss()
                        },
                        label: {
                            LabelText(text: "Done")
                        }
                    )
                }
            }
        }
    }
    
    fileprivate func SettingsForm() -> Form<TupleView<(Section<some View, some View, EmptyView>, Section<some View, some View, EmptyView>, some View)>> {
        return Form {
            
            Section(header: Text("App Theme").bodyMediumFont(size: 16)) {
                
                Picker(
                    selection: $viewModel.appTheme,
                    label: Text("Change Theme")
                        .bodyMediumFont(size: 16),
                    content: {
                        ForEach(AppTheme.allCases, id: \.self) { theme in
                            
                            Text(theme.getName())
                                .tag(theme.rawValue)
                            
                        }
                    })
                .onChange(of: viewModel.appTheme) { theme in
                    viewModel.dispatchAction(action: ThemeSelected(theme: theme.toTheme()))
                }
            }
            
            Section(header: Text("Trakt Account").bodyMediumFont(size: 16)) {
                
                SettingsItem(
                    image: "person.fill",
                    title: "Connect to Trakt",
                    description: "Trakt is a platform that does many things, but primarily keeps track of TV shows and movies you watch."
                ) {
                    showingAlert = true
                }
                .alert(isPresented: $showingAlert) {
                    Alert(
                        title: Text("Trakt Coming Soon"),
                        message: Text("Trakt is a platform that does many things, but primarily keeps track of TV shows and movies you watch."),
                        primaryButton: .default(Text("Login")) { model.initiateAuthorization() },
                        secondaryButton: .destructive(Text("Cancel"))
                    )
                }
            }
            
            Section(header: Text("Info").bodyMediumFont(size: 16)) {
                
                SettingsItem(
                    image: "info.circle.fill",
                    title: "About TvManiac",
                    description: "Tv-Maniac is a Multiplatform app (Android &amp; iOS) for viewing TV Shows from Trakt."
                ) {
                    openURL(URL(string: "https://github.com/c0de-wizard/tv-maniac")!)
                }
            }
            .navigationBarTitle("Settings")
            .background(Color.background)
            .accentColor(Color.accent)
            .onAppear { viewModel.startStateMachine() }
            .onDisappear { viewModel.cancel() }
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


struct SettingsUIView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsUIView()
    }
}
