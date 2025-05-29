import SafariServices
import SwiftUI

public struct SFSafariViewWrapper: UIViewControllerRepresentable {
    private let url: URL

    public init(url: URL) {
        self.url = url
    }

    public func makeUIViewController(context _: UIViewControllerRepresentableContext<Self>) -> SFSafariViewController {
        SFSafariViewController(url: url)
    }

    public func updateUIViewController(
        _: SFSafariViewController,
        context _: UIViewControllerRepresentableContext<SFSafariViewWrapper>
    ) {}
}
