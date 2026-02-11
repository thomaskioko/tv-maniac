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
        .package(name: "CoreKit", path: "../CoreKit"),
        .package(url: "https://github.com/kean/Nuke", exact: "12.8.0"),
        .package(url: "https://github.com/SvenTiigi/YouTubePlayerKit.git", from: "2.0.5"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "SwiftUIComponents",
            dependencies: [
                "CoreKit",
                .product(name: "Nuke", package: "Nuke"),
                .product(name: "NukeUI", package: "Nuke"),
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
