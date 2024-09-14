// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "TvManiacUI",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .library(
            name: "TvManiacUI",
            targets: ["TvManiacUI"]
        )
    ],
    dependencies: [
        .package(name: "SwiftUIComponents", path: "../SwiftUIComponents"),
        .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib")
    ],
    targets: [
        .target(
            name: "TvManiacUI",
            dependencies: [
                "SwiftUIComponents"
            ]
        ),
        .testTarget(
            name: "TvManiacUITests",
            dependencies: [
                "SnapshotTestingLib",
                "TvManiacUI"
            ],
            exclude: ["__Snapshots__"]
        )
    ]
)
