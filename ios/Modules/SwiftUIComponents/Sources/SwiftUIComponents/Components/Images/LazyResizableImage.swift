import Nuke
import NukeUI
import SwiftUI

public struct LazyResizableImage<Content: View>: View {
    private let url: URL?
    private let fixedSize: CGSize?
    @ViewBuilder private let content: (LazyImageState) -> Content

    @State private var resizeProcessor: ImageProcessors.Resize?
    @State private var debouncedTask: Task<Void, Never>?

    public init(
        url: String?,
        size: CGSize? = nil,
        @ViewBuilder content: @escaping (LazyImageState) -> Content
    ) {
        self.url = ImageConfiguration.transformURL(url ?? "")
        fixedSize = size
        self.content = content
    }

    public var body: some View {
        if let fixedSize {
            LazyImage(url: url) { state in
                content(state)
            }
            .processors([.resize(size: fixedSize, unit: .points)])
        } else {
            GeometryReader { proxy in
                LazyImage(url: url) { state in
                    content(state)
                }
                .processors([
                    resizeProcessor ?? .resize(size: proxy.size),
                ])
                .onChange(of: proxy.size, initial: true) { oldValue, newValue in
                    guard oldValue != newValue else { return }
                    updateResizing(with: newValue)
                }
            }
        }
    }

    private func updateResizing(with newSize: CGSize) {
        debouncedTask?.cancel()
        debouncedTask = Task {
            do {
                try await Task.sleep(for: .milliseconds(200))
            } catch { return }
            await MainActor.run {
                resizeProcessor = .resize(size: newSize)
            }
        }
    }
}

public extension LazyResizableImage {
    init(url: String?, size: CGSize? = nil) where Content == AnyView {
        self.init(url: url, size: size) { state in
            AnyView(
                Group {
                    if let image = state.image {
                        image.resizable()
                    } else {
                        Rectangle().fill(.gray.opacity(0.3))
                    }
                }
            )
        }
    }
}
