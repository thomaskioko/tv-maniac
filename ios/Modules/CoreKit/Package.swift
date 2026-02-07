// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "CoreKit",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "CoreKit",
            targets: ["CoreKit"]
        ),
    ],
    targets: [
        .target(
            name: "CoreKit"
        ),
    ]
)
