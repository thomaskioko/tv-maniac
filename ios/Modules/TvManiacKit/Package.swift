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
        .package(name: "SwiftUIComponents", path: "../SwiftUIComponents"),
        .package(name: "TraktAuthKit", path: "../TraktAuthKit"),
        .package(url: "https://github.com/firebase/firebase-ios-sdk", exact: "12.10.0"),
    ],
    targets: [
        .target(
            name: "TvManiacKit",
            dependencies: [
                "CoreKit",
                "SwiftUIComponents",
                "TraktAuthKit",
                .product(name: "FirebaseCore", package: "firebase-ios-sdk"),
            ]
        ),
    ]
)
