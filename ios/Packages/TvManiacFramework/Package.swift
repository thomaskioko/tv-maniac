// swift-tools-version: 6.0

import Foundation
import PackageDescription

/// The binary target points into gradle build output on purpose: the built
/// XCFramework is the one source, with no copy inside this package. The
/// escaping relative path is not covered by Apple or SwiftPM documentation
/// but resolves and builds on Xcode 26.5 (tested); revisit if an Xcode
/// update starts rejecting it.
///
/// Build the framework from the repo root:
///   ./scripts/build-kmp-framework.sh
/// The app's scheme pre-action rebuilds it automatically; rebuild by hand
/// after Kotlin changes (or after `./gradlew clean`, which deletes it) when
/// working on a package on its own.
///
/// The guard below is intentional: without it a missing framework fails
/// resolution with SwiftPM's short "does not contain a binary artifact"
/// message. Failing the manifest with instructions is clearer.
/// TvManiacTestTags is a second, much smaller framework built from :core:test-tags.
/// Only the UI test bundle links it, so the tests read the same tag constants the app
/// applies instead of keeping their own copy of the strings.
func frameworkPath(_ name: String) -> String {
    URL(fileURLWithPath: #filePath)
        .deletingLastPathComponent()
        .appendingPathComponent("../../../ios-framework/build/spm/\(name).xcframework")
        .standardized
        .path
}

for name in ["TvManiac", "TvManiacTestTags"] where !FileManager.default.fileExists(atPath: frameworkPath(name)) {
    fatalError("""
    Missing \(name).xcframework (fresh checkout, or ./gradlew clean removed it).
    Build the KMP frameworks from the repo root:
        ./scripts/build-kmp-framework.sh
    then re-resolve packages (File > Packages > Resolve Package Versions).
    """)
}

let package = Package(
    name: "TvManiacFramework",
    platforms: [
        .iOS(.v18),
    ],
    products: [
        .library(
            name: "TvManiac",
            targets: ["TvManiac"]
        ),
        .library(
            name: "TvManiacTestTags",
            targets: ["TvManiacTestTags"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "TvManiac",
            path: "../../../ios-framework/build/spm/TvManiac.xcframework"
        ),
        .binaryTarget(
            name: "TvManiacTestTags",
            path: "../../../ios-framework/build/spm/TvManiacTestTags.xcframework"
        ),
    ]
)
