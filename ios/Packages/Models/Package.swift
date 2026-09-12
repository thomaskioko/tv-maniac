// swift-tools-version: 6.2

import PackageDescription

let package = Package(
    name: "Models",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v26),
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
        .testTarget(
            name: "ModelsTests",
            dependencies: ["Models"],
            resources: [.process("Fixtures")]
        ),
    ],
    swiftLanguageModes: [.v5]
)
