// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "SwiftUIComponents",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "SwiftUIComponents",
            targets: ["SwiftUIComponents"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Models", path: "../Models"),
        .package(name: "Components", path: "../Components"),
        .package(url: "https://github.com/kean/Nuke", exact: "12.9.0"),
        .package(url: "https://github.com/SvenTiigi/YouTubePlayerKit.git", from: "2.0.5"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "SwiftUIComponents",
            dependencies: [
                "DesignSystem",
                "Models",
                "Components",
                .product(name: "Nuke", package: "Nuke"),
                .product(name: "NukeUI", package: "Nuke"),
                "YouTubePlayerKit",
            ],
            resources: [
                .process("Resources"),
            ]
        ),
        .testTarget(
            name: "SwiftUIComponentsTests",
            dependencies: [
                "SnapshotTestingLib",
                "SwiftUIComponents",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
