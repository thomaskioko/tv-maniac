import Combine
import SwiftUI

public struct CarouselView<T, Content: View>: View {
    private let items: [T]
    @Binding private var currentIndex: Int
    private let onItemScrolled: (T) -> Void
    private let onDraggingChanged: ((Bool) -> Void)?
    private let content: (Int) -> Content

    @Environment(\.scenePhase) private var scenePhase
    @State private var timerCancellable: Cancellable?
    @State private var scrollPosition: Int?

    public init(
        items: [T],
        currentIndex: Binding<Int>,
        onItemScrolled: @escaping (T) -> Void,
        onDraggingChanged: ((Bool) -> Void)? = nil,
        content: @escaping (Int) -> Content
    ) {
        self.items = items
        _currentIndex = currentIndex
        self.onItemScrolled = onItemScrolled
        self.onDraggingChanged = onDraggingChanged
        self.content = content
    }

    public var body: some View {
        GeometryReader { geometry in
            let itemWidth = geometry.size.width

            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack(spacing: 0) {
                    ForEach(items.indices, id: \.self) { index in
                        content(index)
                            .frame(width: itemWidth)
                            .id(index)
                    }
                }
                .scrollTargetLayout()
            }
            .scrollTargetBehavior(.paging)
            .scrollPosition(id: $scrollPosition)
            .onChange(of: scrollPosition) { _, newValue in
                if let newIndex = newValue {
                    currentIndex = newIndex
                    notifyActiveItem()
                }
                stopAutoScroll()
                setupAutoScroll()
                onDraggingChanged?(false)
            }
            .onChange(of: currentIndex) { _, newValue in
                if scrollPosition != newValue {
                    withAnimation(.easeOut(duration: 0.8)) {
                        scrollPosition = newValue
                    }
                }
            }
            .onAppear {
                scrollPosition = currentIndex
            }
        }
        .onAppear {
            setupAutoScroll()
            notifyActiveItem()
        }
        .onDisappear {
            stopAutoScroll()
        }
        .onChange(of: scenePhase) { _, newPhase in
            handleScenePhaseChange(newPhase)
        }
    }

    private func handleScenePhaseChange(_ phase: ScenePhase) {
        switch phase {
        case .active:
            setupAutoScroll()
        case .inactive, .background:
            stopAutoScroll()
        @unknown default:
            break
        }
    }

    private func setupAutoScroll() {
        guard !items.isEmpty else { return }
        stopAutoScroll()
        let itemCount = items.count
        timerCancellable = Timer.publish(every: 5, on: .main, in: .common)
            .autoconnect()
            .sink { _ in
                withAnimation(.easeOut(duration: 0.8)) {
                    currentIndex = (currentIndex + 1) % itemCount
                }
            }
    }

    private func stopAutoScroll() {
        timerCancellable?.cancel()
        timerCancellable = nil
    }

    private func notifyActiveItem() {
        guard !items.isEmpty, items.indices.contains(currentIndex) else { return }
        onItemScrolled(items[currentIndex])
    }
}
