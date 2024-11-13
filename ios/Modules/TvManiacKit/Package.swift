// swift-tools-version: 5.10

import PackageDescription

let package = Package(
  name: "TvManiacKit",
  defaultLocalization: "en",
  platforms: [
    .iOS(.v16),
  ],
  products: [
    .library(
      name: "TvManiacKit",
      targets: ["TvManiacKit", "TvManiac"]
    ),
  ],
  targets: [
    .binaryTarget(
      name: "TvManiac",
      path: "TvManiac.xcframework"
    ),
    .target(
      name: "TvManiacKit",
      dependencies: ["TvManiac"]
    ),
  ]
)

