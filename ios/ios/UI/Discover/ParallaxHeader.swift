import SwiftUI


struct ParallaxHeader<Content: View, Space: Hashable>: View {
  let content: () -> Content
  let coordinateSpace: Space
  let defaultHeight: CGFloat
  let onScroll: (CGFloat) -> Void
  
  init(
    coordinateSpace: Space,
    defaultHeight: CGFloat,
    onScroll: @escaping (CGFloat) -> Void = { _ in },
    @ViewBuilder _ content: @escaping () -> Content
  ) {
    self.content = content
    self.coordinateSpace = coordinateSpace
    self.defaultHeight = defaultHeight
    self.onScroll = onScroll
  }
  
  var body: some View {
    GeometryReader { proxy in
      let frame = proxy.frame(in: .named(coordinateSpace))
      let offset = offset(for: proxy)
      let heightModifier = heightModifier(for: proxy)
      let blurRadius = min(
        heightModifier / 20,
        max(10, heightModifier / 20)
      )
      content()
        .edgesIgnoringSafeArea(.horizontal)
        .frame(
          width: proxy.size.width,
          height: proxy.size.height + heightModifier
        )
        .offset(y: offset)
        .blur(radius: blurRadius)
        .onChange(of: frame.minY) { newOffset in
          onScroll(newOffset)
        }
      
    }
    .frame(height: defaultHeight)
  }
  
  private func offset(for proxy: GeometryProxy) -> CGFloat {
    let frame = proxy.frame(in: .named(coordinateSpace))
    if frame.minY < 0 {
      return -frame.minY * 0.8
    }
    return -frame.minY
  }
  
  private func heightModifier(for proxy: GeometryProxy) -> CGFloat {
    let frame = proxy.frame(in: .named(coordinateSpace))
    return max(0, frame.minY)
  }
  
}
