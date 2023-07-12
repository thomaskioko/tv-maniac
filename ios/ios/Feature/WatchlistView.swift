//
//  WatchlistView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI

struct WatchlistView: View {
    var body: some View {
        ZStack {
            VStack {
                Text("Watchlist")
                
                Spacer()
            }
            .frame(width : CGFloat(480.0))
            .background(Color("Background"))
        }
    }
}

struct WatchlistView_Previews: PreviewProvider {
    static var previews: some View {
        WatchlistView()
        
        WatchlistView()
            .preferredColorScheme(.dark)
    }
}
