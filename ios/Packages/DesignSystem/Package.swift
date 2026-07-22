// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "DesignSystem",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v18),
    ],
    products: [
        .library(
            name: "DesignSystem",
            targets: ["DesignSystem"]
        ),
    ],
    targets: [
        .target(
            name: "DesignSystem",
            resources: [
                .process("Resources/Fonts"),
                .process("Resources/Assets.xcassets"),
            ]
        ),
    ],
    swiftLanguageModes: [.v5]
)
