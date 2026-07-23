// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "Models",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v18),
    ],
    products: [
        .library(
            name: "Models",
            targets: ["Models"]
        ),
    ],
    targets: [
        .target(
            name: "Models"
        ),
    ],
    swiftLanguageModes: [.v5]
)
