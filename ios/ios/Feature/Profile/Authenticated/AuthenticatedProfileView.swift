//
//  AuthenticatedProfileView.swift
//  tv-maniac
//
//  Created by Kioko on 03/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct AuthenticatedProfileView: View {
    
    @ObservedObject private var model = ProfileViewModel()
    
    var body: some View {
        ZStack {
            VStack {
                Text("Coming Soon ...")
                    //TODO:: Add UI
                Spacer()
            }
            .frame(width : CGFloat(480.0))
            .background(Color("Background"))
            
        }
    }
}

struct AuthenticatedProfileView_Previews: PreviewProvider {
    static var previews: some View {
        AuthenticatedProfileView()
    }
}
