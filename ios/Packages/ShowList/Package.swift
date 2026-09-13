// swift-tools-version: 6.2

import PackageDescription

let package = Package(
    name: "ShowList",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v26),
    ],
    products: [
        .library(
            name: "ShowList",
            targets: ["ShowList"]
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
            name: "ShowList",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "TvManiacKit",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ]
        ),
        .testTarget(
            name: "ShowListTests",
            dependencies: [
                "SnapshotTestingLib",
                "ShowList",
                "DesignSystem",
                "Components",
                "Models",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ],
            exclude: ["__Snapshots__"]
        ),
    ],
    swiftLanguageModes: [.v5]
)
