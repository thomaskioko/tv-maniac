// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Root",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "Root",
            targets: ["Root"]
        ),
    ],
    dependencies: [
        .package(name: "DesignSystem", path: "../DesignSystem"),
        .package(name: "Components", path: "../Components"),
        .package(name: "TvManiacKit", path: "../TvManiacKit"),
        .package(name: "Home", path: "../Home"),
        .package(name: "ShowDetails", path: "../ShowDetails"),
        .package(name: "SeasonDetails", path: "../SeasonDetails"),
        .package(name: "Search", path: "../Search"),
        .package(name: "Settings", path: "../Settings"),
        .package(name: "Debug", path: "../Debug"),
        .package(name: "FeatureFlags", path: "../FeatureFlags"),
        .package(name: "MoreShows", path: "../MoreShows"),
        .package(name: "ShowList", path: "../ShowList"),
        .package(name: "EpisodeDetail", path: "../EpisodeDetail"),
        .package(name: "RatingSheet", path: "../RatingSheet"),
    ],
    targets: [
        .target(
            name: "Root",
            dependencies: [
                "DesignSystem",
                "Components",
                "TvManiacKit",
                "Home",
                "ShowDetails",
                "SeasonDetails",
                "Search",
                "Settings",
                "Debug",
                "FeatureFlags",
                "MoreShows",
                "ShowList",
                "EpisodeDetail",
                "RatingSheet",
            ]
        ),
    ]
)
