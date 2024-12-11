import SwiftUI
import SwiftUIComponents
import TvManiacUI

/// A view that displays a horizontally scrolling carousel of show posters
/// with auto-scrolling and interactive gesture support.
struct CarouselView: View {
  // MARK: - Properties

  /// The collection of shows to display in the carousel
  let items: [SwiftShow]

  /// The current index of the displayed item, accounting for wraparound items
  @Binding var currentIndex: Int

  /// Called when the active item changes, either through scrolling or gestures
  let onItemScrolled: (SwiftShow) -> Void

  /// Called when a carousel item is tapped
  let onItemTapped: (Int64) -> Void

  // MARK: - Private Properties
  @GestureState private var dragOffset: CGFloat = 0
  @State private var offset: CGFloat = 0
  @State private var dragging = false

  /// The time interval between auto-scrolls in seconds
  private let autoScrollInterval: TimeInterval = 6.0
  @State private var autoScrollTimer: Timer?

  init(
    items: [SwiftShow],
    currentIndex: Binding<Int>,
    onItemScrolled: @escaping (SwiftShow) -> Void,
    onItemTapped: @escaping (Int64) -> Void
  ) {
    self.items = items
    self._currentIndex = currentIndex
    self.onItemScrolled = onItemScrolled
    self.onItemTapped = onItemTapped
  }

  // MARK: - Private Methods

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

  /// The items to display, including wraparound items for infinite scrolling
  private var displayItems: [SwiftShow] {
    [items.last!] + items + [items.first!]
  }

  var body: some View {
    ZStack(alignment: .bottom) {
      GeometryReader { geometry in
        let screenWidth = geometry.size.width

        HStack(spacing: 0) {
          ForEach(displayItems.indices, id: \.self) { index in
            CarouselItemView(
              item: displayItems[index],
              onTap: {
                onItemTapped(displayItems[index].tmdbId)
              }
            )
            .frame(width: screenWidth)
          }
        }
        .offset(x: offset + dragOffset)
        .onChange(of: currentIndex) { _ in
          resetOffsetIfNeeded(geometry: geometry)
          notifyActiveItem()
        }
        .onAppear {
          currentIndex = 1
          offset = -screenWidth
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

  /// A view that displays a single item in the carousel
  @ViewBuilder
  private func CarouselItemView(item: SwiftShow, onTap: @escaping () -> Void) -> some View {

    GeometryReader { geometry in
      let scrollViewHeight = geometry.size.height

      ZStack(alignment: .bottom) {
        ScrollView(showsIndicators: false) {
          GeometryReader { imageGeometry in
            let minY = imageGeometry.frame(in: .global).minY
            let scrollOffset = minY - geometry.frame(in: .global).minY
            let stretchFactor = max(0, scrollOffset)

            PosterItemView(
              title: item.title,
              posterUrl: item.posterUrl,
              posterWidth: geometry.size.width,
              posterHeight: scrollViewHeight + stretchFactor
            )
            .offset(y: -stretchFactor)
          }
          .frame(height: scrollViewHeight)
        }
        .ignoresSafeArea()
        .onTapGesture {
          onTap()
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

 private struct ScrollOffsetPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
      value = nextValue()
    }
  }

}
