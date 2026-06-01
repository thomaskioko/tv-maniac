// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Search",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Search",
            targets: ["Search"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "Models", path: "../Models"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "Search",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
            ]
        ),
        .testTarget(
            name: "SearchTests",
            dependencies: [
                "SnapshotTestingLib",
                "Search",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
