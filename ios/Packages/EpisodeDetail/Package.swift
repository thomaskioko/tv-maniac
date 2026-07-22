// swift-tools-version: 6.0

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
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
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
        .testTarget(
            name: "EpisodeDetailTests",
            dependencies: [
                "SnapshotTestingLib",
                "EpisodeDetail",
                "DesignSystem",
            ],
            exclude: ["__Snapshots__"]
        ),
    ],
    swiftLanguageModes: [.v5]
)
