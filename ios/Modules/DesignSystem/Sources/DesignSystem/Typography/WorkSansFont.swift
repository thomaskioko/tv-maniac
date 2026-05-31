import CoreText
import OSLog
import SwiftUI

/// Work Sans weights, each mapped to the unique PostScript name embedded in the bundled font file.
public enum WorkSans: String, CaseIterable {
    case thin = "WorkSans-Thin"
    case light = "WorkSans-Light"
    case regular = "WorkSans-Regular"
    case medium = "WorkSans-Medium"
    case semibold = "WorkSans-SemiBold"
    case bold = "WorkSans-Bold"
    case extrabold = "WorkSans-ExtraBold"

    public var postScriptName: String {
        rawValue
    }

    /// Bundled `.ttf` filename (without extension) backing this weight.
    fileprivate var fileName: String {
        switch self {
        case .thin: "work_sans_thin"
        case .light: "work_sans_light"
        case .regular: "work_sans"
        case .medium: "work_sans_medium"
        case .semibold: "work_sans_semibold"
        case .bold: "work_sans_bold"
        case .extrabold: "work_sans_extrabold"
        }
    }
}

public extension Font {
    /// Builds a Work Sans font that scales with Dynamic Type relative to `textStyle`.
    /// Requires `WorkSansFontRegistrar.registerIfNeeded()` to have run first.
    static func workSans(_ weight: WorkSans, size: CGFloat, relativeTo textStyle: Font.TextStyle) -> Font {
        .custom(weight.postScriptName, size: size, relativeTo: textStyle)
    }
}

/// Registers the bundled Work Sans faces with CoreText so `Font.custom(_:)` resolves them by
/// PostScript name. SwiftPM resource bundles are not auto-registered via Info.plist `UIAppFonts`,
/// so explicit process-scoped registration is required before the first font is built.
public enum WorkSansFontRegistrar {
    private static let lock = NSLock()
    private static var didRegister = false
    private static let logger = Logger(subsystem: "com.thomaskioko.tvmaniac", category: "Fonts")

    /// Idempotent and thread-safe. Registers every Work Sans weight once per process.
    public static func registerIfNeeded() {
        lock.lock()
        defer { lock.unlock() }
        guard !didRegister else { return }
        didRegister = true
        WorkSans.allCases.forEach(register)
    }

    private static func register(_ weight: WorkSans) {
        guard let url = Bundle.module.url(forResource: weight.fileName, withExtension: "ttf") else {
            logger.error("Missing bundled font: \(weight.fileName, privacy: .public).ttf")
            return
        }
        var error: Unmanaged<CFError>?
        guard !CTFontManagerRegisterFontsForURL(url as CFURL, .process, &error) else { return }
        guard let cfError = error?.takeRetainedValue() else { return }
        // kCTFontManagerErrorAlreadyRegistered (105) is benign on re-entry across bundles.
        if CFErrorGetCode(cfError) != 105 {
            logger
                .error(
                    "Failed to register \(weight.postScriptName, privacy: .public): \(cfError.localizedDescription, privacy: .public)"
                )
        }
    }
}
