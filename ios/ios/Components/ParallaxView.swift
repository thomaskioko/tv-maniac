//
//  CustomParallaxView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/27/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

/**
 * This implementation is heavily borrowed from this implementation.
 * @see https://medium.com/swlh/swiftui-create-a-stretchable-header-with-parallax-scrolling-4a98faeeb262
 */
struct ParallaxView<Header: View, Content: View>: View {
    let title: String
    let isRefreshing: Bool
    let imageHeight: CGFloat
    let collapsedImageHeight: CGFloat
    let header: (GeometryProxy) -> Header
    let content: (Binding<CGRect>) -> Content
    let onBackClicked: () -> Void
    let onRefreshClicked: () -> Void

    @State private var scrollOffset: CGFloat = 0
    @State private var titleRect: CGRect = .zero
    @State private var headerImageRect: CGRect = .zero
    @ObservedObject private var contentFrame: ViewFrame = ViewFrame()

    init(
        title: String,
        isRefreshing: Bool,
        imageHeight: CGFloat,
        collapsedImageHeight: CGFloat,
        @ViewBuilder header: @escaping (GeometryProxy) -> Header,
        @ViewBuilder content: @escaping (Binding<CGRect>) -> Content,
        onBackClicked: @escaping () -> Void,
        onRefreshClicked: @escaping () -> Void
    ) {
        self.title = title
        self.isRefreshing = isRefreshing
        self.imageHeight = imageHeight
        self.collapsedImageHeight = collapsedImageHeight
        self.header = header
        self.content = content
        self.onBackClicked = onBackClicked
        self.onRefreshClicked = onRefreshClicked
    }

    var body: some View {
        ZStack {
            Color.background

            ScrollView(showsIndicators: false) {
                content($titleRect)
                    .offset(y: imageHeight)
                    .background(GeometryGetter(rect: $contentFrame.frame))

                HeaderView()
            }

            TopBar(
                progress: getTitleOpacity(),
                title: title,
                isRefreshing: isRefreshing,
                onBackClicked: onBackClicked,
                onRefreshClicked: onRefreshClicked
            )
            .background(Color.background.opacity(getTitleOpacity()))
            .zIndex(1)
        }
        .edgesIgnoringSafeArea(.all)
    }

    private func HeaderView() -> some View {
        GeometryReader { proxy in
            ZStack(alignment: .bottom) {
                self.header(proxy)
                    .frame(width: proxy.size.width, height: getHeightForHeaderImage(proxy))
                    .blur(radius: getBlurRadiusForImage(proxy))
                    .clipped()
                    .background(GeometryGetter(rect: $headerImageRect))

                Text(title)
                    .titleFont(size: 20)
                    .foregroundColor(.text_color_bg)
                    .lineLimit(1)
                    .padding(.horizontal, 24)
                    .padding(.bottom, 20)
                    .offset(x: 0, y: self.getHeaderTitleOffset())
                    .opacity(self.getTitleOpacity(proxy))
            }
            .clipped()
            .offset(x: 0, y: getOffsetForHeaderImage(proxy))
        }
        .frame(height: imageHeight)
        .offset(x: 0, y: -(contentFrame.startingRect?.maxY ?? UIScreen.main.bounds.height))
    }

    private func getTitleOpacity() -> CGFloat {
        let progress = -scrollOffset / (imageHeight - collapsedImageHeight)
        return min(1, max(0, progress))
    }


    private func getProgress(_ geometry: GeometryProxy) -> CGFloat {
        let minY = geometry.frame(in: .global).minY
        let progress = -minY / (imageHeight - collapsedImageHeight)
        return min(1, max(0, progress))
    }


    func getScrollOffset(_ geometry: GeometryProxy) -> CGFloat {
        geometry.frame(in: .global).minY
    }

    func getHeightForHeaderImage(_ geometry: GeometryProxy) -> CGFloat {
        let offset = getScrollOffset(geometry)
        let imageHeight = geometry.size.height

        if offset > 0 {
            return imageHeight + offset
        }

        return imageHeight
    }


    func getBlurRadiusForImage(_ geometry: GeometryProxy) -> CGFloat {
        let offset = geometry.frame(in: .global).maxY
        let height = geometry.size.height
        let blur = (height - max(offset, 0)) / height
        return blur * 6
    }

    private func getHeaderTitleOffset() -> CGFloat {
        let currentYPos = titleRect.midY

        if currentYPos < headerImageRect.maxY {
            let minYValue: CGFloat = 50.0
            let maxYValue: CGFloat = collapsedImageHeight
            let currentYValue = currentYPos

            let percentage = max(-1, (currentYValue - maxYValue) / (maxYValue - minYValue))
            let finalOffset: CGFloat = -30.0

            return 20 - (percentage * finalOffset)
        }

        return .infinity
    }

    func getOffsetForHeaderImage(_ geometry: GeometryProxy) -> CGFloat {
        let offset = getScrollOffset(geometry)
        let sizeOffScreen = imageHeight - collapsedImageHeight

        if offset < -sizeOffScreen {
            let imageOffset = abs(min(-sizeOffScreen, offset))
            return imageOffset - sizeOffScreen
        }

        if offset > 0 {
            return -offset
        }

        return 0
    }

    private func getTitleOpacity(_ geometry: GeometryProxy) -> Double {
        let progress = getProgress(geometry)
        return Double(min(1, max(0, progress)))
    }
}

class ViewFrame: ObservableObject {
    var startingRect: CGRect?

    @Published var frame: CGRect {
        willSet {
            if startingRect == nil {
                startingRect = newValue
            }
        }
    }

    init() {
        self.frame = .zero
    }
}

struct GeometryGetter: View {
    @Binding var rect: CGRect

    var body: some View {
        GeometryReader { geometry in
            AnyView(Color.clear)
                .preference(key: RectanglePreferenceKey.self, value: geometry.frame(in: .global))
        }.onPreferenceChange(RectanglePreferenceKey.self) { (value) in
            self.rect = value
        }
    }
}

struct RectanglePreferenceKey: PreferenceKey {
    static var defaultValue: CGRect = .zero

    static func reduce(value: inout CGRect, nextValue: () -> CGRect) {
        value = nextValue()
    }
}
