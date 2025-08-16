import CoreGraphics
import CoreText
import Foundation
import UIKit

public enum FontRegistration {
    private static var fontsRegistered = false

    public static func registerFonts() {
        guard !fontsRegistered else { return }

        let fontNames = [
            "WorkSans-Black",
            "WorkSans-Bold",
            "WorkSans-ExtraBold",
            "WorkSans-ExtraLight",
            "WorkSans-Light",
            "WorkSans-Medium",
            "WorkSans-Regular",
            "WorkSans-SemiBold",
            "WorkSans-Thin",
            "FjallaOne-Regular",
        ]

        for fontName in fontNames {
            registerFont(named: fontName)
        }

        fontsRegistered = true
    }

    private static func registerFont(named name: String) {
        let fontPath = "Fonts/\(name).ttf"
        guard let fontURL = Bundle.module.url(forResource: fontPath, withExtension: nil) else {
            print("Failed to find font file: \(fontPath)")
            // Debug: print bundle structure
            if let resourceURL = Bundle.module.resourceURL {
                print("Bundle resource URL: \(resourceURL)")
                let fontsURL = resourceURL.appendingPathComponent("Fonts")
                if let contents = try? FileManager.default.contentsOfDirectory(at: fontsURL, includingPropertiesForKeys: nil) {
                    print("Available fonts in Fonts directory: \(contents.map(\.lastPathComponent))")
                }
            }
            return
        }

        guard let fontDataProvider = CGDataProvider(url: fontURL as CFURL) else {
            print("Failed to create font data provider for: \(name)")
            return
        }

        guard let font = CGFont(fontDataProvider) else {
            print("Failed to create font from data provider: \(name)")
            return
        }

        var error: Unmanaged<CFError>?
        if !CTFontManagerRegisterGraphicsFont(font, &error) {
            if let error = error?.takeRetainedValue() {
                let errorDescription = CFErrorCopyDescription(error) as String?
                print("Failed to register font \(name): \(errorDescription ?? "Unknown error")")
            }
        }
    }
}
