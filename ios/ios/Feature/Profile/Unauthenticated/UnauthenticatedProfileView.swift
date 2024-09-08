    //
    //  UnauthenticatedView.swift
    //  tv-maniac
    //
    //  Created by Kioko on 03/04/2023.
    //  Copyright © 2023 orgName. All rights reserved.
    //

import SwiftUI

struct UnauthentivatedProfileView : View {
    
    @ObservedObject private var model = TraktAuthViewModel()
    
    var body: some View {
        VStack {
            
            if self.model.error != nil {
                //TODO:: Show Error message
            }
            
            Image(uiImage: UIImage(named: "trakt_logo")!)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 120, height: 120)
                .padding(.bottom, 16)
            
            Divider()
                .frame(height: 2.0)
                .overlay(Color.accent)

            Text("Trakt is a platform that does many things, but primarily keeps track of TV shows and movies you watch. By signing in, you will have access to the follwing:")
                .bodyMediumFont(size: 16)
                .multilineTextAlignment(.center)
                .padding(8)
            
            BulletList(
                listItemSpacing: 10,
                bulletWidth: 20
            )
            .padding(.bottom, 16)
            
            Button(
                action: { model.initiateAuthorization()},
                label: {
                    Text("Connect To Trakt")
                        .captionFont(size: 16)
                })
            .foregroundColor(.white)
            .padding()
            .background(Color.accent)
            .cornerRadius(8)
            
            Spacer()
        }
        .background(Color("Background"))
        .padding(16)
    }
}

struct BulletList: View {
    
    var listItemSpacing: CGFloat? = nil
    var bullet: String = "•"
    var bulletWidth: CGFloat? = nil
    var bulletAlignment: Alignment = .leading
    
    var listItems: [String] = [
        "Sync shows that you are watching.",
        "View watch history.",
        "Check the weekly release schedule.",
        "More feature coming soon."
    ]
    
    var body: some View {
        VStack(alignment: .leading,
               spacing: listItemSpacing) {
            ForEach(listItems, id: \.self) { data in
                HStack(alignment: .top) {
                    Text(bullet)
                        .titleSemiBoldFont(size: 20)
                        .foregroundColor(.accentColor)

                    Text(data)
                        .captionStyle(size: 16)
                }
            }
        }
    }
}

struct UnauthentivatedView_Previews: PreviewProvider {
    static var previews: some View {
        UnauthentivatedProfileView()
        
        UnauthentivatedProfileView()
            .preferredColorScheme(.dark)
    }
}
