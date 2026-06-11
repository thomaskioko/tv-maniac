import Foundation

public extension Bundle {
    /// The DesignSystem resource bundle, exposed so other packages can load its asset-catalog brand marks
    /// (e.g. `TraktMono`, `SimklMono`).
    static var designSystem: Bundle {
        .module
    }
}
