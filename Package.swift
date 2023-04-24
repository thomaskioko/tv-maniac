// swift-tools-version:5.3
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://maven.pkg.github.com/c0de-wizard/tv-maniac/tv-maniac/shared/base-kmmbridge/0.2.5.1/base-kmmbridge-0.2.5.1.zip"
let remoteKotlinChecksum = "aa384a0b0bb459b7d6daa71392f5ca5974378bc3aa96d1728fda65f35fd3e831"
let packageName = "TvManiac"
// END KMMBRIDGE BLOCK

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
            checksum: remoteKotlinChecksum
        )
        ,
    ]
)