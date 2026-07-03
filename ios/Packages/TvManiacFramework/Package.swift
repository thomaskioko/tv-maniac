// swift-tools-version: 5.10

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
let frameworkPath = URL(fileURLWithPath: #filePath)
    .deletingLastPathComponent()
    .appendingPathComponent("../../../ios-framework/build/spm/TvManiac.xcframework")
    .standardized
    .path
guard FileManager.default.fileExists(atPath: frameworkPath) else {
    fatalError("""
    Missing TvManiac.xcframework (fresh checkout, or ./gradlew clean removed it).
    Build the KMP framework from the repo root:
        ./scripts/build-kmp-framework.sh
    then re-resolve packages (File > Packages > Resolve Package Versions).
    """)
}

let package = Package(
    name: "TvManiacFramework",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "TvManiac",
            targets: ["TvManiac"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "TvManiac",
            path: "../../../ios-framework/build/spm/TvManiac.xcframework"
        ),
    ]
)
