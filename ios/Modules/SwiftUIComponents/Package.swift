// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "SwiftUIComponents",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16),
    ],
    products: [
        .library(
            name: "SwiftUIComponents",
            targets: ["SwiftUIComponents"]
        ),
    ],
    dependencies: [
        .package(url: "https://github.com/onevcat/Kingfisher.git", from: "8.0.0"),
        .package(url: "https://github.com/SvenTiigi/YouTubePlayerKit.git", from: "2.0.5"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "SwiftUIComponents",
            dependencies: [
                .product(name: "Kingfisher", package: "Kingfisher"),
                "YouTubePlayerKit",
            ]
        ),
        .testTarget(
            name: "SwiftUIComponentsTests",
            dependencies: [
                "SnapshotTestingLib",
                "SwiftUIComponents",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
