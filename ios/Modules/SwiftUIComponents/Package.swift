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
        .package(url: "https://github.com/SDWebImage/SDWebImageSwiftUI.git", from: "3.1.3"),
        .package(url: "https://github.com/SvenTiigi/YouTubePlayerKit.git", from: "2.0.1"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "SwiftUIComponents",
            dependencies: [
                "SDWebImageSwiftUI",
                "YouTubePlayerKit",
            ],
            resources: [
                .copy("Resources/Fonts"),
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
