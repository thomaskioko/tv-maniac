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
  dependencies: [
      .package(name: "SwiftUIComponents", path: "../SwiftUIComponents"),
      .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
  ],
  targets: [
    .binaryTarget(
      name: "TvManiac",
      path: "TvManiac.xcframework"
    ),
    .target(
      name: "TvManiacKit",
      dependencies: [
        "SwiftUIComponents",
        "TvManiac",
      ]
    ),
    .testTarget(
        name: "TvManiacKitTests",
        dependencies: [
            "SnapshotTestingLib",
            "TvManiacKit"
        ],
        exclude: ["__Snapshots__"]
    )
  ]
)

