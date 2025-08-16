import Foundation

public extension String {
    var transformedImageURL: String {
        ImageURLTransformer.transform(self)
    }
}
