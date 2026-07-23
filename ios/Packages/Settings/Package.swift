// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "Settings",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v18),
    ],
    products: [
        .library(
            name: "Settings",
            targets: ["Settings"]
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
            name: "Settings",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "TvManiacKit",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ],
            resources: [
                .process("Resources"),
            ]
        ),
        .testTarget(
            name: "SettingsTests",
            dependencies: [
                "SnapshotTestingLib",
                "Settings",
                "DesignSystem",
                "Components",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ],
    swiftLanguageModes: [.v5]
)
