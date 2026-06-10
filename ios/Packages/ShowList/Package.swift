// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "ShowList",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
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
    ],
    targets: [
        .target(
            name: "ShowList",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "TvManiacKit",
            ]
        ),
    ]
)
