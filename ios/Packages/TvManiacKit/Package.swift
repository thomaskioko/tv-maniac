// swift-tools-version: 6.2

import PackageDescription

let package = Package(
    name: "TvManiacKit",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v26),
    ],
    products: [
        .library(
            name: "TvManiacKit",
            targets: ["TvManiacKit"]
        ),
    ],
    dependencies: [
        .package(name: "CoreKit", path: "../CoreKit"),
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Models", path: "../Models"),
        .package(name: "Components", path: "../Components"),
        .package(name: "TraktAuthKit", path: "../TraktAuthKit"),
        .package(name: "TvManiacFramework", path: "../TvManiacFramework"),
    ],
    targets: [
        .target(
            name: "TvManiacKit",
            dependencies: [
                "CoreKit",
                "DesignSystem",
                "Models",
                "Components",
                "TraktAuthKit",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ]
        ),
    ],
    swiftLanguageModes: [.v5]
)
