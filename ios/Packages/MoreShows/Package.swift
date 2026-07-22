// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "MoreShows",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "MoreShows",
            targets: ["MoreShows"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "Models", path: "../Models"),
        .package(name: "TvManiacKit", path: "../TvManiacKit"),
        .package(name: "TvManiacFramework", path: "../TvManiacFramework"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "MoreShows",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "TvManiacKit",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ]
        ),
        .testTarget(
            name: "MoreShowsTests",
            dependencies: [
                "SnapshotTestingLib",
                "MoreShows",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ],
    swiftLanguageModes: [.v5]
)
