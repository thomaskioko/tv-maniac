import PackageDescription

let remoteKotlinUrl = "https://maven.pkg.github.com/touchlab/KMMBridgeKickStart/co/touchlab/kmmbridgekickstart/allshared-kmmbridge/0.13.0/allshared-kmmbridge-0.13.0.zip"
let remoteKotlinChecksum = "fe57822fa8ae5806e791558b1a632c3cd3af6f185d8e866ff4e132f40f68a6d4"
let packageName = "Tvmaniac"

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