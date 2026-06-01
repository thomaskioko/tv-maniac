// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "TvManiacKit",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
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
        .package(name: "Calendar", path: "../Calendar"),
        .package(name: "Search", path: "../Search"),
        .package(name: "Library", path: "../Library"),
        .package(name: "Profile", path: "../Profile"),
        .package(name: "Settings", path: "../Settings"),
        .package(name: "TraktAuthKit", path: "../TraktAuthKit"),
        .package(url: "https://github.com/firebase/firebase-ios-sdk", exact: "12.14.0"),
    ],
    targets: [
        .target(
            name: "TvManiacKit",
            dependencies: [
                "CoreKit",
                "DesignSystem",
                "Models",
                "Components",
                "Calendar",
                "Search",
                "Library",
                "Profile",
                "Settings",
                "TraktAuthKit",
                .product(name: "FirebaseCore", package: "firebase-ios-sdk"),
            ]
        ),
    ]
)
