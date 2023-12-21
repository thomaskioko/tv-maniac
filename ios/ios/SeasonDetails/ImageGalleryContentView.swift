//
//  ImageGalleryContentView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct ImageGalleryContentView: View {
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        Text("Click Me")
            .onTapGesture {
                presentationMode.wrappedValue.dismiss()
            }
    }
}

#Preview {
    ImageGalleryContentView()
}
