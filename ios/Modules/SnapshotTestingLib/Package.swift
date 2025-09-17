// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "SnapshotTestingLib",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16),
    ],
    products: [
        .library(
            name: "SnapshotTestingLib",
            targets: ["SnapshotTestingLib"]
        ),
    ],
    dependencies: [
        .package(url: "https://github.com/pointfreeco/swift-snapshot-testing.git", exact: "1.18.7"),
    ],
    targets: [
        .target(
            name: "SnapshotTestingLib",
            dependencies: [
                .product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
            ]
        ),
    ]
)
