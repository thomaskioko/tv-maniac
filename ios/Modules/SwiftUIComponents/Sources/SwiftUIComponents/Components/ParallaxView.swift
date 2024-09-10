import SwiftUI

/**
 * This implementation is heavily borrowed from this implementation.
 * @see https://medium.com/swlh/swiftui-create-a-stretchable-header-with-parallax-scrolling-4a98faeeb262
 */
public struct ParallaxView<Header: View, Content: View>: View {
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
    @ObservedObject private var contentFrame: ViewFrame = .init()

    public init(
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

    public var body: some View {
        ZStack {
            Color.background

            ScrollView(showsIndicators: false) {
                VStack(spacing: 0) {
                    GeometryReader { proxy in
                        self.header(proxy)
                            .frame(width: proxy.size.width, height: proxy.getHeightForHeaderImage(proxy))
                            .background(GeometryGetter(rect: self.$headerImageRect))
                            .offset(y: self.getOffsetForHeaderImage(proxy))
                    }
                    .frame(height: imageHeight)

                    content($titleRect)
                        .background(GeometryGetter(rect: $contentFrame.frame))
                }
            }
            .background(GeometryGetter(rect: $headerImageRect))

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
                    .frame(width: proxy.size.width, height: proxy.getHeightForHeaderImage(proxy))
                    .blur(radius: proxy.getBlurRadiusForImage(proxy))
                    .clipped()
                    .background(GeometryGetter(rect: $headerImageRect))

                Text(title)
                    .titleFont(size: 20)
                    .foregroundColor(.textColor)
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

public struct GeometryGetter: View {
    @Binding private var rect: CGRect

  public init(rect: Binding<CGRect>){
    self._rect = rect
  }

  public var body: some View {
        GeometryReader { geometry in
            Color.clear
                .preference(key: RectanglePreferenceKey.self, value: geometry.frame(in: .global))
        }
        .onPreferenceChange(RectanglePreferenceKey.self) { value in
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

struct ScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value += nextValue()
    }
}
