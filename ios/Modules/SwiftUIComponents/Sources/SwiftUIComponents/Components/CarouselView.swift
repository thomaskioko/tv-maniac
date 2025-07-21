import Combine
import SwiftUI

/// A view that displays a horizontally scrolling carousel of show posters
/// with auto-scrolling and interactive gesture support.
public struct CarouselView<T, Content: View>: View {
    private let items: [T]
    @Binding private var currentIndex: Int
    private let onItemScrolled: (T) -> Void
    private let onItemTapped: (Int64) -> Void
    private let content: (Int) -> Content

    @State private var timer: Timer.TimerPublisher = Timer.publish(every: 5, on: .main, in: .common)
    @State private var timerCancellable: Cancellable?
    @State private var isDragging: Bool = false

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
                        .simultaneousGesture(
                            DragGesture(minimumDistance: 0)
                                .onChanged { _ in
                                    isDragging = true
                                    stopAutoScroll()
                                }
                                .onEnded { _ in
                                    isDragging = false
                                    setupAutoScroll()
                                }
                        )
                    })
                }
            }
            .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            .onAppear {
                setupAutoScroll()
            }
            .onDisappear {
                stopAutoScroll()
            }
        }
    }

    private func setupAutoScroll() {
        guard !isDragging else {
            return
        }
        timer = Timer.publish(every: 5, on: .main, in: .common)
        timerCancellable = timer.autoconnect().sink { _ in
            withAnimation(.easeOut(duration: 0.8)) {
                currentIndex = (currentIndex + 1) % items.count
            }
        }
    }

    private func stopAutoScroll() {
        timerCancellable?.cancel()
        timerCancellable = nil
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
