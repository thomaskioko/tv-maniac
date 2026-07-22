// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "ShowDetails",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "ShowDetails",
            targets: ["ShowDetails"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "Models", path: "../Models"),
        .package(name: "TvManiacKit", path: "../TvManiacKit"),
        .package(name: "UpNext", path: "../UpNext"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "ShowDetails",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "UpNext",
                "TvManiacKit",
            ]
        ),
        .testTarget(
            name: "ShowDetailsTests",
            dependencies: [
                "SnapshotTestingLib",
                "ShowDetails",
                "DesignSystem",
                "Components",
                "Models",
                "UpNext",
            ],
            exclude: ["__Snapshots__"]
        ),
    ],
    swiftLanguageModes: [.v5]
)
