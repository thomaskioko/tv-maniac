// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "WatchDateSelection",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v18),
    ],
    products: [
        .library(
            name: "WatchDateSelection",
            targets: ["WatchDateSelection"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "TvManiacKit", path: "../TvManiacKit"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "WatchDateSelection",
            dependencies: [
                "DesignSystem",
                "Components",
                "TvManiacKit",
            ]
        ),
        .testTarget(
            name: "WatchDateSelectionTests",
            dependencies: [
                "SnapshotTestingLib",
                "WatchDateSelection",
                "DesignSystem",
            ],
            exclude: ["__Snapshots__"]
        ),
    ],
    swiftLanguageModes: [.v5]
)
