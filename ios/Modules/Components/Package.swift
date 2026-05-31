// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Components",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Components",
            targets: ["Components"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Models", path: "../Models"),
        .package(url: "https://github.com/kean/Nuke", exact: "12.9.0"),
        .package(url: "https://github.com/SvenTiigi/YouTubePlayerKit.git", from: "2.0.5"),
    ],
    targets: [
        .target(
            name: "Components",
            dependencies: [
                "DesignSystem",
                "Models",
                .product(name: "Nuke", package: "Nuke"),
                .product(name: "NukeUI", package: "Nuke"),
                "YouTubePlayerKit",
            ]
        ),
    ]
)
