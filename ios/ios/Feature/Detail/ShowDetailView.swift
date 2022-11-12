//
//  ShowDetailView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct ShowDetailView: View {
	
	//TODO User state from stateMachine and replace viewState reference
//    @ObservedObject var observable = ObservableViewModel<ShowDetailsViewModel, ShowDetailUiViewState>(
//            viewModel: ShowDetailsViewModel()
//    )

    @SwiftUI.State var topEdge: CGFloat = 0
    @SwiftUI.State var offset: CGFloat = 0
    @SwiftUI.State var titleOffset: CGFloat = 0

    @Environment(\.colorScheme) var scheme
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    @GestureState private var dragOffset = CGSize.zero

    var showId: Int32
    let maxHeight = CGFloat(520)

    init(showId: Int32) {
        self.showId = showId
    }

    var body: some View {
        ScrollView(.vertical, showsIndicators: false) {

            VStack {
                GeometryReader { proxy in
                    HeaderView(
                            viewState: viewState,
                            topEdge: topEdge,
                            maxHeight: maxHeight,
                            offset: $offset,
                            onFollowShowClicked: { id in
                                print(id)
                            }
                    )
                            .frame(maxWidth: .infinity)
                            .frame(height: getHeaderHeight(), alignment: .bottom)
                            .background(BackgroundView())
                            .overlay(
                                    TopNavBar(
                                            offset: $offset,
                                            viewState: viewState,
                                            maxHeight: maxHeight,
                                            topEdge: topEdge
                                    )
                                            .padding(.horizontal)
                                            .frame(height: 160)
                                            .foregroundColor(.white)
                                            .padding(.top, topEdge)
                                    , alignment: .top
                            )
                }
                        .frame(height: maxHeight)
                        .offset(y: -offset)
                        .zIndex(1)


                ShowBodyView(viewState: viewState)
                        .offset(y: 100)
                        .zIndex(0)
            }
                    .modifier(OffsetModifier(offset: $offset))
				
        }
                .background(Color.background)
                .coordinateSpace(name: "SCROLL")
                .navigationBarHidden(true)
                .edgesIgnoringSafeArea(.all)
                .gesture(DragGesture().updating($dragOffset, body: { (value, state, transaction) in
                    if (value.startLocation.x < 20 && value.translation.width > 100) {
                        mode.wrappedValue.dismiss()
                    }
                }))
    }

    @ViewBuilder
    func BackgroundView() -> some View {

        let color: Color = (scheme == .dark ? .black : .white)
        // Custom Gradient
        LinearGradient(colors: [
            .black,
            .clear,
            color.opacity(0.15),
            color.opacity(0.5),
            color.opacity(0.8),
            color,
            color
        ], startPoint: .top, endPoint: .bottom)

        // Blurred Overlay
        Rectangle()
                .fill(.ultraThinMaterial)
    }

    func getHeaderHeight() -> CGFloat {
        let topHeight = maxHeight + offset

        return topHeight > (120 + topEdge) ? topHeight : (120 + topEdge)
    }

    func getCornerRadius() -> CGFloat {

        let progress = -offset / (maxHeight - (80 + topEdge))

        let radius = (1 - progress) * 50

        return offset < 0 ? radius : 50
    }
}


struct ShowDetailView_Previews: PreviewProvider {
    static var previews: some View {
        ShowDetailView(showId: 1234)
    }
}
