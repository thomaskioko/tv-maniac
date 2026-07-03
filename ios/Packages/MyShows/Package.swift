// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "MyShows",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "MyShows",
            targets: ["MyShows"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "Models", path: "../Models"),
        .package(name: "TvManiacKit", path: "../TvManiacKit"),
        .package(name: "UpNext", path: "../UpNext"),
        .package(name: "TvManiacFramework", path: "../TvManiacFramework"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "MyShows",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "UpNext",
                "TvManiacKit",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ]
        ),
        .testTarget(
            name: "MyShowsTests",
            dependencies: [
                "SnapshotTestingLib",
                "MyShows",
                "DesignSystem",
                "Components",
                "Models",
                "UpNext",
            ],
            exclude: ["__Snapshots__"]
        ),
    ]
)
