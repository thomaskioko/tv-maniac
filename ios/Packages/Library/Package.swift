// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Library",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Library",
            targets: ["Library"]
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
            name: "Library",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
            ]
        ),
        .testTarget(
            name: "LibraryTests",
            dependencies: [
                "SnapshotTestingLib",
                "Library",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
