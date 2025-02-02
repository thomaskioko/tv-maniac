import SwiftUI

/// A view that displays a horizontally scrolling carousel of show posters
/// with auto-scrolling and interactive gesture support.
public struct CarouselView<T, Content: View>: View {
  private let items: [T]
  @Binding private var currentIndex: Int
  private let onItemScrolled: (T) -> Void
  private let onItemTapped: (Int64) -> Void
  private let content: (Int) -> Content

  public init(
    items: [T],
    currentIndex: Binding<Int>,
    onItemScrolled: @escaping (T) -> Void,
    onItemTapped: @escaping (Int64) -> Void,
    content: @escaping (Int) -> Content
  ) {
    self.items = items
    _currentIndex = currentIndex
    self.onItemScrolled = onItemScrolled
    self.onItemTapped = onItemTapped
    self.content = content
  }

  public var body: some View {
    ZStack(alignment: .bottom) {
      TabView(selection: $currentIndex) {
        ForEach(items.indices, id: \.self) { index in
          ZStack(alignment: Alignment(horizontal: .trailing, vertical: .bottom), content: {
            GeometryReader { reader in
              let screenWidth = reader.size.width
              
              HStack(spacing: 0) {
                content(index)
                  .offset(x: -reader.frame(in: .global).minX)
                  .frame(width: screenWidth)
              }
              .onChange(of: currentIndex) { _ in
                notifyActiveItem()
              }
              .onAppear {
                notifyActiveItem()
              }
            }
            .cornerRadius(0)
            .shadow(color: Color.black.opacity(0.2), radius: 5, x: 5, y: 5)
            .shadow(color: Color.black.opacity(0.2), radius: 5, x: -5, y: -5)
          })
        }
      }
      .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
    }
  }

  private func notifyActiveItem() {
    onItemScrolled(items[currentIndex])
  }
}

private struct ScrollOffsetPreferenceKey: PreferenceKey {
  static var defaultValue: CGFloat = 0
  static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
    value = nextValue()
  }
}
