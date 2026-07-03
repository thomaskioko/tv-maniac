# TvManiacFramework

Swift package that exposes the KMP-generated XCFramework (`TvManiac`) to SwiftPM as a binary target. Every consumer resolves the shared Kotlin code through this package: the application, any package opened on its own (`xed ios/Packages/<Name>`), and CI. The package contains only the manifest; the framework itself lives in gradle build output.

## How linking works

The Kotlin code is compiled by `:ios-framework` into a **static** framework (`isStatic = true`) and packaged as an XCFramework at `ios-framework/build/spm/TvManiac.xcframework`. The manifest points straight at it:

```
.library(name: "TvManiac", targets: ["TvManiac"])
.binaryTarget(name: "TvManiac", path: "../../../ios-framework/build/spm/TvManiac.xcframework")
```

SwiftPM does two things with a binary target:

1. **Compile time**: every target that declares the `TvManiac` product gets the framework's module map and umbrella header on its search paths, which is what makes `import TvManiac` resolve. Packages that do not declare the product still see the re-exported API through `TvManiacKit` (`@_exported import TvManiac`).
2. **Link time**: the framework's static library is linked **once** into the final app executable through the SPM dependency graph. Because the framework is static there is no embed step: the Kotlin code is compiled into the app binary itself, and nothing is copied into `tv-maniac.app/Frameworks/` at runtime.

This replaces the previous direct-linking setup, where the app project pointed `FRAMEWORK_SEARCH_PATHS` at gradle output, force-linked with `-framework TvManiac` in `OTHER_LDFLAGS`, and a pre-action copied the framework into `BUILT_PRODUCTS_DIR` so package targets could find it. All of that is gone; the binary target is the one place both compiling and linking read the framework from.

This is still local integration in the sense JetBrains' [iOS integration overview](https://kotlinlang.org/docs/multiplatform/multiplatform-ios-integration-overview.html) uses the term: the framework is built from the local Kotlin sources on every application build, and nothing is published or versioned. What changed is only the delivery into the build. JetBrains' [local Swift packages recipe](https://kotlinlang.org/docs/multiplatform/multiplatform-spm-local-integration.html), the `embedAndSignAppleFrameworkForXcode` pre-action with packages writing plain imports, is the setup this repository had before; it works only through the application scheme, which is why packages could not open on their own. The binary target keeps the same build model and lets SwiftPM resolve the framework from any package.

The framework must stay singular. It also carries the i18n module (Moko `MR` accessors and runtime), exported from `:ios-framework`. Two overlapping static frameworks linked explicitly by SwiftPM duplicate the shared Kotlin/Moko object files and the app link fails with duplicate symbols (`ResourcesBundleAnchor`). Moko resource bundles are not inside the static library at runtime; the app's Run Script build phase invokes `:i18n:generator:copyFrameworkResourcesToApp` to copy them into the app bundle.

## Why the build-directory path

The binary target points into `ios-framework/build/` so the built XCFramework is the one source, with no copy inside this package. Two trade-offs come with that, both deliberate:

- A relative path escaping the package root is not covered by Apple or SwiftPM documentation. It resolves and builds on Xcode 26.5 (verified with app, standalone, and missing-artifact tests); if a future Xcode starts rejecting it, move the artifact back inside the package and point the manifest at it.
- `./gradlew clean` deletes the artifact, and package resolution runs BEFORE scheme pre-actions, so a missing artifact fails any Xcode build until the script runs once. The fastlane lanes call the script before invoking xcodebuild, so they provision themselves; in Xcode (or when working on a package on its own), run `./scripts/build-kmp-framework.sh`. The failure in the meantime is loud and names the path (`does not contain a binary artifact`, or the manifest's own message with instructions).

## Full application builds

The `tv-maniac` and `Snapshots` schemes run `scripts/build-kmp-framework.sh` as a **build pre-action**, so the framework is rebuilt and put in place before Xcode compiles anything:

1. The pre-action inherits the Xcode build environment (`CONFIGURATION`, `SDK_NAME`) from the app target.
2. The script runs the matching single-slice gradle link task, for example `:ios-framework:linkDebugFrameworkIosSimulatorArm64`. Gradle is incremental: unchanged Kotlin is a no-op.
3. It then combines every slice already built for that configuration (`ios-arm64-simulator`, `ios-arm64`) with `xcodebuild -create-xcframework` and moves the result to `ios-framework/build/spm/TvManiac.xcframework`.
4. Xcode resolves this package, compiles the Swift packages against the fresh framework, and links the static library into the app.

Kotlin changes therefore flow into full app builds automatically; nobody runs the script by hand for the app workflow. A Release archive works the same way: the pre-action sees `CONFIGURATION=Release` and a device SDK, relinks the release device slice, and overwrites the artifact.

One limit: pre-actions refresh an EXISTING artifact, they cannot create a missing one, because package resolution runs before scheme actions. The fastlane lanes therefore run the script before xcodebuild (`build_kmp_framework` in `fastlane/Fastfile`), and a missing artifact in Xcode means running the script once by hand.

## Working on a package on its own

Individual packages have no scheme pre-action, so they use whatever framework is already built:

```bash
./scripts/build-kmp-framework.sh   # debug, arm64 simulator (default)
xed ios/Packages/Search
```

SwiftPM resolves `../TvManiacFramework`, follows the binary target to the built XCFramework, and the package builds, tests, and renders previews on its own. Re-run the script after changing Kotlin code (or after `./gradlew clean`); an outdated framework shows up as compile errors against the Kotlin API, not as a missing module. Other slices when needed:

```bash
./scripts/build-kmp-framework.sh --platform device
./scripts/build-kmp-framework.sh --configuration release --platform device
```

| | Full app | Single package |
|---|---|---|
| Who builds the framework | Scheme pre-action, every build | Developer runs the script |
| Keeping it current | Automatic | Re-run the script after Kotlin changes or `./gradlew clean` |
| Slice | Matches the SDK Xcode is building | Defaults to debug arm64 simulator |

## CI

The `build-ios-framework` job builds the XCFramework with the same script and caches `ios-framework/build/spm`. Consumer jobs restore the cache before invoking `xcodebuild` and set `TVMANIAC_SKIP_FRAMEWORK_BUILD=1`, which makes the pre-action exit early when the required slice is already present.

## Debug vs Release

Both configurations share the single location `ios-framework/build/spm`. The last build wins: the pre-action overwrites the framework with the configuration being built, so a Release archive after local Debug work relinks the release slices on demand.

## Troubleshooting

- **"Missing TvManiac.xcframework" or "does not contain a binary artifact" during resolution**: run `./scripts/build-kmp-framework.sh`, then File > Packages > Resolve Package Versions. Happens on fresh checkouts and after `./gradlew clean`.
- **Compile errors against Kotlin API that you just changed**: the built framework is outdated; re-run the script.
- **Xcode holds on to a replaced framework**: File > Packages > Reset Package Caches, or delete DerivedData.
- **Device build fails with "no library for this platform"**: the XCFramework only has the simulator slice; run the script with `--platform device`.
