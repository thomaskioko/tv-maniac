// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "SwiftUiViews",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16),
    ],
    products: [
        .library(
            name: "SwiftUiViews",
            targets: ["SwiftUiViews"]
        ),
    ],
    targets: [
        .target(
            name: "SwiftUiViews"
        ),
        .testTarget(
            name: "SwiftUiViewsTests",
            dependencies: ["SwiftUiViews"]
        ),
    ]
)
