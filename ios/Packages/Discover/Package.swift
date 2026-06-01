// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Discover",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Discover",
            targets: ["Discover"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "Models", path: "../Models"),
        .package(name: "UpNext", path: "../UpNext"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "Discover",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "UpNext",
            ]
        ),
        .testTarget(
            name: "DiscoverTests",
            dependencies: [
                "SnapshotTestingLib",
                "Discover",
                "DesignSystem",
                "Components",
                "Models",
                "UpNext",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
