import SnapshotTesting
import SwiftUI

public extension SwiftUISnapshotLayout {
    static let defaultDevice = SwiftUISnapshotLayout.device(config: .iPhone13Pro)
}

public struct SnapshotStyles: OptionSet {
    public let rawValue: Int

    public init(rawValue: Int) {
        self.rawValue = rawValue
    }

    public static let light = SnapshotStyles(rawValue: 1 << 0)
    public static let dark = SnapshotStyles(rawValue: 1 << 1)
    public static let all: SnapshotStyles = [.light, .dark]
}

public extension Snapshotting where Value == UIViewController, Format == UIImage {
    static func image(
        precision: Float = 1,
        perceptualPrecision: Float = 1,
        layout: SwiftUISnapshotLayout = .sizeThatFits,
        traits: UITraitCollection = .init()
    ) -> Snapshotting<UIViewController, UIImage> {
        let config = switch layout {
        case let .device(config: deviceConfig): deviceConfig
        case .sizeThatFits: ViewImageConfig(traits: traits)
        case let .fixed(width: width, height: height):
            ViewImageConfig(
                size: CGSize(width: width, height: height),
                traits: traits
            )
        }

        return .image(
            on: config,
            precision: precision,
            perceptualPrecision: perceptualPrecision,
            traits: traits
        )
    }
}

public extension View {
    func assertSnapshot(
        record recording: Bool = false,
        layout: SwiftUISnapshotLayout = .defaultDevice,
        styles: SnapshotStyles = .all,
        timeout: TimeInterval = {
            // Use longer timeout in CI environment
            if ProcessInfo.processInfo.environment["CI"] != nil {
                return 90
            }
            return 30
        }(),
        file: StaticString = #file,
        testName: String
    ) {
        var themes: [String: Snapshotting<UIViewController, UIImage>] = [:]

        if styles.contains(.light) {
            themes["light"] = .image(
                layout: layout,
                traits: UITraitCollection(userInterfaceStyle: .light)
            )
        }

        if styles.contains(.dark) {
            themes["dark"] = .image(
                layout: layout,
                traits: UITraitCollection(userInterfaceStyle: .dark)
            )
        }

        assertSnapshots(
            of: viewController,
            as: themes,
            record: recording,
            timeout: timeout,
            file: file,
            testName: testName
        )
    }

    private var viewController: UIViewController {
        let viewController = UIHostingController(rootView: self)

        let view = viewController.view!
        view.bounds = CGRect(origin: .zero, size: view.intrinsicContentSize)
        view.backgroundColor = .clear

        return viewController
    }
}
