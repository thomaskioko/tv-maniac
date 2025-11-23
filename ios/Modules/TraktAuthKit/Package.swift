// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "TraktAuthKit",
    platforms: [
        .iOS(.v14),
    ],
    products: [
        .library(
            name: "TraktAuthKit",
            targets: ["TraktAuthKit"]
        ),
    ],
    dependencies: [
        .package(
            url: "https://github.com/openid/AppAuth-iOS.git",
            from: "2.0.0"
        ),
    ],
    targets: [
        .target(
            name: "TraktAuthKit",
            dependencies: [
                .product(name: "AppAuth", package: "AppAuth-iOS"),
            ],
            path: "Sources/TraktAuthKit"
        ),
    ]
)
