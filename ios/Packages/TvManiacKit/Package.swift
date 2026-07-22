// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "TvManiacKit",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v18),
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
        .package(url: "https://github.com/firebase/firebase-ios-sdk", exact: "12.16.0"),
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
                .product(name: "FirebaseCore", package: "firebase-ios-sdk"),
            ]
        ),
    ],
    swiftLanguageModes: [.v5]
)
