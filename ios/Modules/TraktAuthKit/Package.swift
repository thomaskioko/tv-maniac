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
            from: "1.7.6"
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
