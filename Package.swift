import PackageDescription

let remoteKotlinUrl = "https://maven.pkg.github.com/c0de-wizard/tv-maniac/tv-maniac/shared-kmmbridge/0.0.1/shared-kmmbridge-0.0.1.zip"
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
