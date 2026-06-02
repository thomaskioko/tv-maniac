// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "UpNext",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "UpNext",
            targets: ["UpNext"]
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
            name: "UpNext",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
            ]
        ),
        .testTarget(
            name: "UpNextTests",
            dependencies: [
                "SnapshotTestingLib",
                "UpNext",
                "Components",
                "DesignSystem",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
