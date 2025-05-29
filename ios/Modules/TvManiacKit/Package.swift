// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "TvManiacKit",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16),
    ],
    products: [
        .library(
            name: "TvManiacKit",
            targets: ["TvManiacKit"]
        ),
    ],
    dependencies: [
        .package(name: "SwiftUIComponents", path: "../SwiftUIComponents"),
    ],
    targets: [
        .target(
            name: "TvManiacKit",
            dependencies: [
                "SwiftUIComponents",
            ]
        ),
    ]
)
