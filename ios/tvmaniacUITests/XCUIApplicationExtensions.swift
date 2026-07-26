import XCTest

extension XCUIApplication {
    static func launchTvManiac(scenario: String = StubScenario.unauthenticated) -> XCUIApplication {
        let app = XCUIApplication()
        app.launchEnvironment["TVMANIAC_STUB_SCENARIO"] = scenario
        app.launchEnvironment["TVMANIAC_FIXTURE_DIR"] = fixtureDirectory
        app.launchEnvironment["TVMANIAC_CLEAR_STATE"] = "1"
        app.launch()
        app.dismissSystemAlertIfPresent()
        return app
    }

    private static var fixtureDirectory: String {
        URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent()
            .deletingLastPathComponent()
            .deletingLastPathComponent()
            .appendingPathComponent("core/integration/stubs/src/commonMain/resources")
            .path
    }

    var tabBar: XCUIElement {
        tabBars.firstMatch
    }

    func screen(_ identifier: String) -> XCUIElement {
        otherElements[identifier]
    }

    func element(_ identifier: String) -> XCUIElement {
        descendants(matching: .any).matching(identifier: identifier).firstMatch
    }

    @discardableResult
    func awaitScreen(
        _ identifier: String,
        timeout: TimeInterval = UITestTimeouts.screen,
        file: StaticString = #filePath,
        line: UInt = #line
    ) -> XCUIElement {
        let element = screen(identifier)
        XCTAssertTrue(
            element.waitForExistence(timeout: timeout),
            "Screen \"\(identifier)\" never appeared within \(timeout)s.",
            file: file,
            line: line
        )
        return element
    }

    func dismissSystemAlertIfPresent() {
        let springboard = XCUIApplication(bundleIdentifier: "com.apple.springboard")
        let alert = springboard.alerts.firstMatch
        guard alert.waitForExistence(timeout: UITestTimeouts.systemAlert) else { return }

        for label in ["Allow", "Allow While Using App", "OK", "Don’t Allow"] {
            let button = alert.buttons[label]
            if button.exists {
                button.tap()
                return
            }
        }
        alert.buttons.firstMatch.tap()
    }
}

enum StubScenario {
    static let unauthenticated = "unauthenticated"
    static let search = "search"
    static let authenticatedTrakt = "authenticatedTrakt"
}

enum UITestTimeouts {
    static let launch: TimeInterval = 90
    static let screen: TimeInterval = 30
    static let systemAlert: TimeInterval = 5
}
