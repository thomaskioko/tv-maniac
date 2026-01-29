import SwiftUI

/**
 * This implementation is heavily borrowed from this implementation.
 * @see https://medium.com/swlh/swiftui-create-a-stretchable-header-with-parallax-scrolling-4a98faeeb262
 */
public struct ParallaxView<Header: View, Content: View>: View {
    @Theme private var theme

    let imageHeight: CGFloat
    let collapsedImageHeight: CGFloat
    let onScroll: (CGFloat) -> Void
    let header: (GeometryProxy) -> Header
    let content: () -> Content

    @State private var scrollOffset: CGFloat = 0
    @State private var headerImageRect: CGRect = .zero
    @State private var contentFrame: CGRect = .zero

    public init(
        imageHeight: CGFloat,
        collapsedImageHeight: CGFloat,
        @ViewBuilder header: @escaping (GeometryProxy) -> Header,
        @ViewBuilder content: @escaping () -> Content,
        onScroll: @escaping (CGFloat) -> Void
    ) {
        self.imageHeight = imageHeight
        self.collapsedImageHeight = collapsedImageHeight
        self.header = header
        self.content = content
        self.onScroll = onScroll
    }

    public var body: some View {
        ZStack {
            theme.colors.background

            ScrollView(showsIndicators: false) {
                VStack(spacing: 0) {
                    GeometryReader { proxy in
                        let offset = proxy.getScrollOffset(proxy)
                        header(proxy)
                            .frame(width: proxy.size.width, height: proxy.getHeightForHeaderImage(proxy))
                            .background(GeometryGetter(rect: $headerImageRect))
                            .offset(y: getOffsetForHeaderImage(proxy))
                            .onChange(of: offset) { newOffset in
                                onScroll(newOffset)
                            }
                    }
                    .frame(height: imageHeight)

                    content()
                        .background(GeometryGetter(rect: $contentFrame))
                }
            }
            .background(GeometryGetter(rect: $headerImageRect))
        }
        .edgesIgnoringSafeArea(.all)
    }

    func getOffsetForHeaderImage(_ geometry: GeometryProxy) -> CGFloat {
        let offset = geometry.getScrollOffset(geometry)
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
}

public struct GeometryGetter: View {
    @Binding private var rect: CGRect

    public init(rect: Binding<CGRect>) {
        _rect = rect
    }

    public var body: some View {
        GeometryReader { geometry in
            Color.clear
                .preference(key: RectanglePreferenceKey.self, value: geometry.frame(in: .global))
        }
        .onPreferenceChange(RectanglePreferenceKey.self) { value in
            rect = value
        }
    }
}

struct RectanglePreferenceKey: PreferenceKey {
    static var defaultValue: CGRect = .zero

    static func reduce(value: inout CGRect, nextValue: () -> CGRect) {
        value = nextValue()
    }
}

struct ScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value += nextValue()
    }
}
