// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Home",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Home",
            targets: ["Home"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "Models", path: "../Models"),
        .package(name: "UpNext", path: "../UpNext"),
        .package(name: "TvManiacKit", path: "../TvManiacKit"),
        .package(name: "Discover", path: "../Discover"),
        .package(name: "MyShows", path: "../MyShows"),
        .package(name: "Profile", path: "../Profile"),
        .package(name: "Calendar", path: "../Calendar"),
        .package(name: "Progress", path: "../Progress"),
        .package(name: "TvManiacFramework", path: "../TvManiacFramework"),
    ],
    targets: [
        .target(
            name: "Home",
            dependencies: [
                "DesignSystem",
                "Components",
                "Models",
                "UpNext",
                "TvManiacKit",
                "Discover",
                "MyShows",
                "Profile",
                "Calendar",
                "Progress",
                .product(name: "TvManiac", package: "TvManiacFramework"),
            ]
        ),
    ]
)
