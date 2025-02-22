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
      targets: ["TvManiacKit"]
    ),
  ],
  dependencies: [
      .package(name: "SwiftUIComponents", path: "../SwiftUIComponents"),
      .package(name: "SnapshotTestingLib", path: "../SnapshotTestingLib"),
  ],
  targets: [
    .target(
      name: "TvManiacKit",
      dependencies: [
        "SwiftUIComponents"
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

