// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Debug",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Debug",
            targets: ["Debug"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "Models", path: "../Models"),
        .package(name: "TvManiacKit", path: "../TvManiacKit"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "Debug",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "TvManiacKit",
            ]
        ),
        .testTarget(
            name: "DebugTests",
            dependencies: [
                "SnapshotTestingLib",
                "Debug",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
