import CoreText
import SwiftUI

public enum FontRegistration {
    public static func register() {
        guard let fontURLs = Bundle.module.urls(forResourcesWithExtension: "ttf", subdirectory: nil) else {
            print("⚠️ Could not find font directory in bundle: \(Bundle.module.bundlePath)")
            return
        }

        for url in fontURLs {
            guard let fontDataProvider = CGDataProvider(url: url as CFURL) else {
                print("⚠️ Could not create font data provider for: \(url)")
                continue
            }

            guard let font = CGFont(fontDataProvider) else {
                print("⚠️ Could not create font from data provider: \(url)")
                continue
            }

            var error: Unmanaged<CFError>?
            if !CTFontManagerRegisterGraphicsFont(font, &error) {
                print("⚠️ Error registering font: \(error.debugDescription)")
            }
        }
    }
}
