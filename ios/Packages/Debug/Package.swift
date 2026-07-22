// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "Debug",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Debug",
            targets: ["Debug"]
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
            name: "Debug",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "TvManiacKit",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ]
        ),
        .testTarget(
            name: "DebugTests",
            dependencies: [
                "SnapshotTestingLib",
                "Debug",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ],
    swiftLanguageModes: [.v5]
)
