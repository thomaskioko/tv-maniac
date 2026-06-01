// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Profile",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Profile",
            targets: ["Profile"]
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
            name: "Profile",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
            ],
            resources: [
                .process("Resources"),
            ]
        ),
        .testTarget(
            name: "ProfileTests",
            dependencies: [
                "SnapshotTestingLib",
                "Profile",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
