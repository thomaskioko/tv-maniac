// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "SnapshotTestingLib",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v18),
    ],
    products: [
        .library(
            name: "SnapshotTestingLib",
            targets: ["SnapshotTestingLib"]
        ),
    ],
    dependencies: [
        .package(url: "https://github.com/pointfreeco/swift-snapshot-testing.git", exact: "1.19.3"),
        .package(name: "DesignSystem", path: "../DesignSystem"),
    ],
    targets: [
        .target(
            name: "SnapshotTestingLib",
            dependencies: [
                .product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
                "DesignSystem",
            ]
        ),
    ],
    swiftLanguageModes: [.v5]
)
