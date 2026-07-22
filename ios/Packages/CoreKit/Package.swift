// swift-tools-version: 6.0

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
    dependencies: [
        .package(name: "TvManiacFramework", path: "../TvManiacFramework"),
        .package(url: "https://github.com/firebase/firebase-ios-sdk", exact: "12.16.0"),
        .package(url: "https://github.com/kean/Nuke", exact: "12.9.0"),
    ],
    targets: [
        .target(
            name: "CoreKit",
            dependencies: [
                .product(name: "TvManiac", package: "TvManiacFramework"),
                .product(name: "FirebaseCrashlytics", package: "firebase-ios-sdk"),
                .product(name: "FirebaseRemoteConfig", package: "firebase-ios-sdk"),
                .product(name: "Nuke", package: "Nuke"),
            ]
        ),
    ],
    swiftLanguageModes: [.v5]
)
