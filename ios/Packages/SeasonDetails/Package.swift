// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "SeasonDetails",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "SeasonDetails",
            targets: ["SeasonDetails"]
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
            name: "SeasonDetails",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
            ]
        ),
        .testTarget(
            name: "SeasonDetailsTests",
            dependencies: [
                "SnapshotTestingLib",
                "SeasonDetails",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
