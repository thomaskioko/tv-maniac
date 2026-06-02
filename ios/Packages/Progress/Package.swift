// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Progress",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Progress",
            targets: ["Progress"]
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
            name: "Progress",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
            ]
        ),
        .testTarget(
            name: "ProgressTests",
            dependencies: [
                "SnapshotTestingLib",
                "Progress",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
