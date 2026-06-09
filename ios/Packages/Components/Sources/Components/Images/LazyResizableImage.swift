import DesignSystem
import Nuke
import NukeUI
import SwiftUI

public struct LazyResizableImage: View {
    private let url: URL?
    private let fixedSize: CGSize?
    private let aspectRatio: CGFloat?
    private let placeholderIcon: String
    private let placeholderTitle: String?

    @State private var resizeProcessor: ImageProcessors.Resize?
    @State private var debouncedTask: Task<Void, Never>?

    public init(
        url: String?,
        imageType: TmdbImageType? = nil,
        size: CGSize? = nil,
        aspectRatio: CGFloat? = nil,
        placeholderIcon: String = "popcorn.fill",
        placeholderTitle: String? = nil
    ) {
        self.url = ImageConfiguration.transformURL(url ?? "", imageType: imageType)
        fixedSize = size
        self.aspectRatio = aspectRatio
        self.placeholderIcon = placeholderIcon
        self.placeholderTitle = placeholderTitle
    }

    public var body: some View {
        loader
            .modifier(AspectRatioSizing(width: fixedSize?.width, aspectRatio: aspectRatio))
    }

    @ViewBuilder
    private var loader: some View {
        if let fixedSize {
            LazyImage(url: url) { state in
                imageContent(state, size: fixedSize)
            }
            .processors([.resize(size: fixedSize, unit: .points)])
        } else {
            GeometryReader { proxy in
                LazyImage(url: url) { state in
                    imageContent(state, size: proxy.size)
                }
                .processors([
                    resizeProcessor ?? .resize(size: proxy.size),
                ])
                .onChange(of: proxy.size, initial: true) { oldValue, newValue in
                    guard oldValue != newValue else { return }
                    updateResizing(with: newValue)
                }
                .onDisappear {
                    debouncedTask?.cancel()
                    debouncedTask = nil
                }
            }
        }
    }

    @ViewBuilder
    private func imageContent(_ state: LazyImageState, size: CGSize) -> some View {
        if let image = state.image {
            image.resizable()
        } else {
            PosterPlaceholder(
                title: placeholderTitle,
                icon: placeholderIcon,
                posterWidth: size.width,
                posterHeight: size.height,
                posterRadius: 0,
                shadowToken: TvManiacShadowToken.none
            )
        }
    }

    private func updateResizing(with newSize: CGSize) {
        debouncedTask?.cancel()
        debouncedTask = Task {
            do {
                try await Task.sleep(for: .milliseconds(200))
            } catch { return }

            guard !Task.isCancelled else { return }

            await MainActor.run {
                resizeProcessor = .resize(size: newSize)
            }
        }
    }
}

private struct AspectRatioSizing: ViewModifier {
    let width: CGFloat?
    let aspectRatio: CGFloat?

    func body(content: Content) -> some View {
        if let width, let aspectRatio {
            content
                .scaledToFill()
                .frame(width: width, height: width / aspectRatio)
                .clipped()
        } else {
            content
        }
    }
}
