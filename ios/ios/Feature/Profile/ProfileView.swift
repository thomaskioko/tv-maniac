    //
    //  ProfileView.swift
    //  tv-maniac
    //
    //  Created by Kioko on 03/04/2023.
    //  Copyright © 2023 orgName. All rights reserved.
    //

import SwiftUI

struct ProfileView: View {
    
    @ObservedObject private var model = ProfileViewModel()
    @State var isPresented = false
    
    
    var body: some View {
        NavigationView {
            
            if(!model.isAuthenticated){
                UnauthentivatedProfileView()
                    .toolbar{
                        ToolbarItem(placement: .navigationBarTrailing) {
                            
                            Button(
                                action: { self.isPresented = true },
                                label: {
                                    Image(systemName: "gearshape")
                                        .resizable()
                                        .aspectRatio(contentMode: .fit)
                                        .foregroundColor(.grey_200)
                                        .frame(width: 24, height: 24)
                                }
                            )
                            .sheet(
                                isPresented: $isPresented,
                                content: { SettingsUIView() }
                            )
                        }
                    }
            } else {
                AuthenticatedProfileView()
            }
        }
        
    }
}

struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileView()
    }
}
