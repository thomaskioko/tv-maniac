import SwiftUI
import SwiftUIComponents
import TvManiacUI

struct CarouselView: View {
  let items: [SwiftShow]
  @Binding var currentIndex: Int
  let onItemScrolled: (SwiftShow) -> Void
  let onItemTapped: (Int64) -> Void
  @GestureState private var dragOffset: CGFloat = 0
  @State private var offset: CGFloat = 0
  @State private var dragging = false

  //TODO:: Determine if we should autoScroll from Settings.
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

  private var displayItems: [SwiftShow] {
    [items.last!] + items + [items.first!]
  }

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

  private func notifyActiveItem() {
    let actualIndex = (currentIndex - 1) % items.count
    onItemScrolled(items[actualIndex])
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
            }
            .onEnded { value in
              dragging = false
              let velocity = value.predictedEndTranslation.width - value.translation.width
              let translation = value.translation.width

              if abs(velocity) > 100 || abs(translation) > screenWidth * 0.2 {
                let direction: CGFloat = translation > 0 ? -1 : 1
                let newIndex = currentIndex + Int(direction)

                // Calculate animation duration based on remaining distance and velocity
                let remainingDistance = screenWidth - abs(translation)
                let velocityFactor = max(abs(velocity) / 1000, 1.0)
                let animationDuration = Double(remainingDistance) / (1000 * velocityFactor)
                let clampedDuration = min(max(0.2, animationDuration), 0.3)

                withAnimation(.easeOut(duration: clampedDuration)) {
                  currentIndex = newIndex
                  offset = -CGFloat(newIndex) * screenWidth
                }
              } else {
                withAnimation(.easeOut(duration: 0.2)) {
                  offset = -CGFloat(currentIndex) * screenWidth
                }
              }

              startAutoScroll()
            }
        )
        .animation(dragging ? nil : .easeOut(duration: 0.2), value: dragOffset)
      }
      CustomIndicator(items)
        .padding()
        .padding(.top, 10)
    }

  }

  @ViewBuilder
  func CustomIndicator(_ shows: [SwiftShow]) -> some View {
    HStack(spacing: 5) {
      ForEach(shows.indices, id: \.self) { index in
        Circle()
          .fill(currentIndex - 1 == index ? Color.accent : .gray.opacity(0.5))
          .frame(width: currentIndex - 1 == index ? 10 : 6, height: currentIndex - 1 == index ? 10 : 6)
      }
    }
    .animation(.easeInOut, value: currentIndex)
  }

  struct CarouselItemView: View {
    let item: SwiftShow
    let onTap: () -> Void

    var body: some View {
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

          VStack(alignment: .leading) {
            Text(item.title)
              .font(.system(size: 46, weight: .bold))
              .foregroundColor(.textColor)
              .lineLimit(1)
              .frame(maxWidth: .infinity, alignment: .center)


            if let overview = item.overview {
              Text(overview)
                .font(.avenirNext(size: 17))
                .foregroundColor(.textColor)
                .multilineTextAlignment(.leading)
                .lineLimit(2)
            }
          }
          .padding(.horizontal)
          .padding(.bottom, 40)
          .frame(maxWidth: .infinity, alignment: .leading)
          .background(
            LinearGradient(
              gradient: Gradient(
                colors: [
                  .black,
                  .black.opacity(0.8),
                  .black.opacity(0.6),
                  .black.opacity(0.4),
                  .clear
                ]
              ),
              startPoint: .bottom,
              endPoint: .top
            )
            .frame(height: 300)
            .allowsHitTesting(false)
          )
        }
      }
    }
  }

 private struct ScrollOffsetPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
      value = nextValue()
    }
  }

}
