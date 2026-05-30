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
            name: "DesignSystem",
            targets: ["DesignSystem"]
        ),
        .library(
            name: "SwiftUIComponents",
            targets: ["SwiftUIComponents"]
        ),
    ],
    dependencies: [
        .package(url: "https://github.com/kean/Nuke", exact: "12.9.0"),
        .package(url: "https://github.com/SvenTiigi/YouTubePlayerKit.git", from: "2.0.5"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "DesignSystem",
            dependencies: [],
            resources: [
                .process("Resources/Fonts"),
            ]
        ),
        .target(
            name: "SwiftUIComponents",
            dependencies: [
                "DesignSystem",
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
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
