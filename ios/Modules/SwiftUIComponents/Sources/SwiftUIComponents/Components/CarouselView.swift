import SwiftUI

/// A view that displays a horizontally scrolling carousel of show posters
/// with auto-scrolling and interactive gesture support.
public struct CarouselView<T, Content: View>: View {
  private let items: [T]
  @Binding private var currentIndex: Int
  private let onItemScrolled: (T) -> Void
  private let onItemTapped: (Int64) -> Void
  private let content: (Int) -> Content

  @GestureState private var dragOffset: CGFloat = 0
  @State private var offset: CGFloat = 0
  @State private var dragging = false

  /// The time interval between auto-scrolls in seconds
  private let autoScrollInterval: TimeInterval = 6.0
  @State private var autoScrollTimer: Timer?

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

  /// The items to display, including wraparound items for infinite scrolling
  private var displayItems: [T] {
    guard let first = items.first, let last = items.last else { return [] }
    return [last] + items + [first]
  }

  public var body: some View {
    ZStack(alignment: .bottom) {
      GeometryReader { geometry in
        let screenWidth = geometry.size.width

        HStack(spacing: 0) {
          ForEach(displayItems.indices, id: \.self) { index in
            let actualIndex = (index - 1) % items.count
            content(actualIndex >= 0 ? actualIndex : items.count - 1)
              .frame(width: screenWidth)
          }
        }
        .offset(x: offset + dragOffset)
        .onChange(of: currentIndex) { _ in
          resetOffsetIfNeeded(geometry: geometry)
          notifyActiveItem()
        }
        .onAppear {
          offset = -CGFloat(currentIndex) * screenWidth
          startAutoScroll()
          notifyActiveItem()
        }
        .onDisappear {
          autoScrollTimer?.invalidate()
        }
        .gesture(
          DragGesture()
            .updating($dragOffset) { value, state, _ in
              state = value.translation.width
              if !dragging {
                dragging = true
                autoScrollTimer?.invalidate()
              }

              handleDragUpdate(
                translation: value.translation.width,
                screenWidth: screenWidth
              )
            }
            .onEnded { value in
              dragging = false
              let velocity = value.predictedEndTranslation.width - value.translation.width
              let translation = value.translation.width

              handleDragEnd(
                velocity: velocity,
                translation: translation,
                screenWidth: screenWidth
              )
            }
        )
        .animation(dragging ? nil : .easeOut, value: dragOffset)
      }
    }
  }

  /// Starts the auto-scroll timer if not already running
  private func startAutoScroll() {
    autoScrollTimer?.invalidate()
    autoScrollTimer = Timer.scheduledTimer(withTimeInterval: autoScrollInterval, repeats: true) { _ in
      if !dragging {
        withAnimation(.easeOut(duration: 0.6)) {
          currentIndex += 1
          offset = -CGFloat(currentIndex) * UIScreen.main.bounds.width
        }
      }
    }
  }

  /// Resets the offset when reaching the end to enable infinite scrolling
  /// - Parameter geometry: The geometry proxy providing the container's dimensions
  private func resetOffsetIfNeeded(geometry: GeometryProxy) {
    let screenWidth = geometry.size.width
    if currentIndex == displayItems.count - 1 {
      withAnimation(.none) {
        currentIndex = 1
        offset = -screenWidth
      }
    }
    if currentIndex == 0 {
      withAnimation(.none) {
        currentIndex = items.count
        offset = -CGFloat(items.count) * screenWidth
      }
    }
  }

  /// Notifies observers about the currently active item
  private func notifyActiveItem() {
    let actualIndex = (currentIndex - 1) % items.count
    onItemScrolled(items[actualIndex])
  }

  /// Handles the drag gesture state update
  private func handleDragUpdate(
    translation: CGFloat,
    screenWidth: CGFloat
  ) {
    let halfScreenWidth = screenWidth / 2

    if abs(translation) > halfScreenWidth {
      let direction = translation > 0 ? -1 : 1
      let nextIndex = (currentIndex + direction - 1) % items.count
      guard nextIndex >= 0 && nextIndex < items.count else { return }
      onItemScrolled(items[nextIndex])
    } else {
      let actualIndex = (currentIndex - 1) % items.count
      guard actualIndex >= 0 && actualIndex < items.count else { return }
      onItemScrolled(items[actualIndex])
    }
  }

  /// Handles the end of a drag gesture
  private func handleDragEnd(
    velocity: CGFloat,
    translation: CGFloat,
    screenWidth: CGFloat
  ) {
    if abs(velocity) > 100 || abs(translation) > screenWidth * 0.2 {
      let direction: Int = translation > 0 ? -1 : 1
      let targetIndex = currentIndex + direction
      currentIndex = targetIndex
      offset = -CGFloat(targetIndex) * screenWidth
    } else {
      offset = -CGFloat(currentIndex) * screenWidth
    }

    DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) {
      startAutoScroll()
    }
  }
}

private struct ScrollOffsetPreferenceKey: PreferenceKey {
  static var defaultValue: CGFloat = 0
  static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
    value = nextValue()
  }
}
