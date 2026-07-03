// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Search",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Search",
            targets: ["Search"]
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
            name: "Search",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "TvManiacKit",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ]
        ),
        .testTarget(
            name: "SearchTests",
            dependencies: [
                "SnapshotTestingLib",
                "Search",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
