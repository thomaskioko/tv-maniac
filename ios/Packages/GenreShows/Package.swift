// swift-tools-version: 6.2

import PackageDescription

let package = Package(
    name: "GenreShows",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v26),
    ],
    products: [
        .library(
            name: "GenreShows",
            targets: ["GenreShows"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "Models", path: "../Models"),
        .package(name: "TvManiacKit", path: "../TvManiacKit"),
        .package(name: "TvManiacFramework", path: "../TvManiacFramework"),
        .package(name: "MoreShows", path: "../MoreShows"),
    ],
    targets: [
        .target(
            name: "GenreShows",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "TvManiacKit",
                "MoreShows",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ]
        ),
    ],
    swiftLanguageModes: [.v5]
)
