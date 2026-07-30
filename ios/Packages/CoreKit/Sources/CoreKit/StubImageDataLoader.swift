import Foundation
import Nuke
import UIKit

struct StubImageDataLoader: DataLoading {
    private static let placeholder: Data = {
        let size = CGSize(width: 8, height: 12)
        let image = UIGraphicsImageRenderer(size: size).image { context in
            UIColor.darkGray.setFill()
            context.fill(CGRect(origin: .zero, size: size))
        }
        return image.pngData() ?? Data()
    }()

    func loadData(
        with request: URLRequest,
        didReceiveData: @escaping @Sendable (Data, URLResponse) -> Void,
        completion: @escaping @Sendable (Error?) -> Void
    ) -> any Cancellable {
        let data = Self.placeholder
        let response = URLResponse(
            url: request.url ?? URL(fileURLWithPath: "/"),
            mimeType: "image/png",
            expectedContentLength: data.count,
            textEncodingName: nil
        )
        didReceiveData(data, response)
        completion(nil)
        return NoopCancellable()
    }
}

private final class NoopCancellable: Cancellable {
    func cancel() {}
}
