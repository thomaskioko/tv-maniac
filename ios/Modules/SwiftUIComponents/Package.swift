// swift-tools-version: 5.10

import PackageDescription

let package = Package(
  name: "SwiftUIComponents",
  defaultLocalization: "en",
  platforms: [
    .iOS(.v16)
  ],
  products: [
    .library(
      name: "SwiftUIComponents",
      targets: ["SwiftUIComponents"]
    )
  ],
  dependencies: [
    .package(url: "https://github.com/SDWebImage/SDWebImageSwiftUI.git", from: "3.1.2"),
    .package(url: "https://github.com/SvenTiigi/YouTubePlayerKit.git", from: "1.9.0")
  ],
  targets: [
    .target(
      name: "SwiftUIComponents",
      dependencies: [
        "SDWebImageSwiftUI",
        "YouTubePlayerKit"
      ],
      resources: [
        .process("Fonts")
      ]
    )
  ]
)
