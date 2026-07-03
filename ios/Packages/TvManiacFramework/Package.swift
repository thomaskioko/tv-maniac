// swift-tools-version: 5.10

import Foundation
import PackageDescription

/// The XCFramework is generated, not committed. Build it from the repo root:
///   ./scripts/build-kmp-framework.sh
/// The app's scheme pre-action rebuilds it automatically; rebuild by hand
/// after Kotlin changes when working on a package on its own.
///
/// The guard below is intentional: without it a missing framework fails
/// resolution with SwiftPM's short "artifact not found" message. Failing the
/// manifest with instructions is clearer for the developer.
let frameworkPath = URL(fileURLWithPath: #filePath)
    .deletingLastPathComponent()
    .appendingPathComponent("Frameworks/TvManiac.xcframework")
    .path
guard FileManager.default.fileExists(atPath: frameworkPath) else {
    fatalError("""
    Missing TvManiac.xcframework.
    Build the KMP framework first, from the repo root:
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
            path: "Frameworks/TvManiac.xcframework"
        ),
    ]
)
