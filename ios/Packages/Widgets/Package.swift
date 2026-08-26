// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "Widgets",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v18),
    ],
    products: [
        .library(
            name: "Widgets",
            targets: ["Widgets"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Models", path: "../Models"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
    ],
    targets: [
        .target(
            name: "Widgets",
            dependencies: [
                "DesignSystem",
                "Models",
            ]
        ),
        .testTarget(
            name: "WidgetsTests",
            dependencies: [
                "SnapshotTestingLib",
                "Widgets",
                "DesignSystem",
                "Models",
            ],
            exclude: ["__Snapshots__"]
        ),
    ],
    swiftLanguageModes: [.v5]
)
