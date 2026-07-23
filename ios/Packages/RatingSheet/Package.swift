// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "RatingSheet",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v18),
    ],
    products: [
        .library(
            name: "RatingSheet",
            targets: ["RatingSheet"]
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
            name: "RatingSheet",
            dependencies: [
                "DesignSystem",
                "Components",
                "TvManiacKit",
            ]
        ),
        .testTarget(
            name: "RatingSheetTests",
            dependencies: [
                "SnapshotTestingLib",
                "RatingSheet",
                "DesignSystem",
            ],
            exclude: ["__Snapshots__"]
        ),
    ],
    swiftLanguageModes: [.v5]
)
