import Foundation

/// A static configuration for transforming image URLs
/// This allows the app to inject URL transformation logic without creating a dependency
public enum ImageURLTransformer {
    /// The transformation closure that can be set by the app
    private static var transformer: ((String) -> String)?

    /// Sets the URL transformer
    /// - Parameter transformer: A closure that transforms image URLs
    public static func setTransformer(_ transformer: @escaping (String) -> String) {
        self.transformer = transformer
    }

    /// Transforms an image URL using the configured transformer
    /// - Parameter url: The original URL
    /// - Returns: The transformed URL, or the original if no transformer is set
    public static func transform(_ url: String) -> String {
        transformer?(url) ?? url
    }

    public static func reset() {
        transformer = nil
    }
}
