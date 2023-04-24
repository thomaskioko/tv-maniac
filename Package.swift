// swift-tools-version:5.3
import PackageDescription

let remoteKotlinUrl = "https://github.com/c0de-wizard/tv-maniac/packages/1845300" //TODO Remove hardcoded url
let packageName = "TvManiac"

let package = Package(
    name: packageName,
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: packageName,
            targets: [packageName]
        ),
    ],
    targets: [
        .binaryTarget(
            name: packageName,
            url: remoteKotlinUrl,
        )
        ,
    ]
)