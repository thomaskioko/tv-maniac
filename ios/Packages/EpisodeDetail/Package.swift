// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "EpisodeDetail",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "EpisodeDetail",
            targets: ["EpisodeDetail"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "TvManiacKit", path: "../TvManiacKit"),
    ],
    targets: [
        .target(
            name: "EpisodeDetail",
            dependencies: [
                "DesignSystem",
                "Components",
                "TvManiacKit",
            ]
        ),
    ]
)
