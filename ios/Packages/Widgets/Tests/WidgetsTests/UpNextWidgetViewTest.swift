import DesignSystem
import SnapshotTestingLib
import SwiftUI
import WidgetKit
@testable import Widgets
import XCTest

final class UpNextWidgetViewTest: SnapshotTestCase {
    private let items = [
        UpNextItem(
            id: 1396,
            showName: "Breaking Bad",
            episodeName: "Pilot",
            seasonEpisodeLabel: "S01 | E01"
        ),
        UpNextItem(
            id: 60059,
            showName: "Better Call Saul",
            episodeName: "Uno",
            seasonEpisodeLabel: "S01 | E01"
        ),
        UpNextItem(
            id: 1399,
            showName: "Game of Thrones",
            episodeName: "Winter Is Coming",
            seasonEpisodeLabel: "S01 | E01"
        ),
        UpNextItem(
            id: 82856,
            showName: "The Mandalorian",
            episodeName: "Chapter 1",
            seasonEpisodeLabel: "S01 | E01"
        ),
    ]

    func testSmallContent() {
        assert(
            state: .content(items: items, lastUpdated: nil),
            family: .systemSmall,
            name: "UpNextWidget_Small_Content"
        )
    }

    func testMediumContent() {
        assert(
            state: .content(items: items, lastUpdated: nil),
            family: .systemMedium,
            name: "UpNextWidget_Medium_Content"
        )
    }

    func testLargeContent() {
        assert(
            state: .content(items: items, lastUpdated: nil),
            family: .systemLarge,
            name: "UpNextWidget_Large_Content"
        )
    }

    func testLargeContentWithLastUpdated() {
        assert(
            state: .content(items: items, lastUpdated: "Updated 2 hours ago"),
            family: .systemLarge,
            name: "UpNextWidget_Large_LastUpdated"
        )
    }

    func testSmallEmpty() {
        assert(
            state: .empty(message: "Nothing to watch next. Track a show to see it here."),
            family: .systemSmall,
            name: "UpNextWidget_Small_Empty"
        )
    }

    func testMediumEmpty() {
        assert(
            state: .empty(message: "Nothing to watch next. Track a show to see it here."),
            family: .systemMedium,
            name: "UpNextWidget_Medium_Empty"
        )
    }

    func testSmallPlaceholder() {
        assert(state: .placeholder, family: .systemSmall, name: "UpNextWidget_Small_Placeholder")
    }

    func testMediumPlaceholder() {
        assert(state: .placeholder, family: .systemMedium, name: "UpNextWidget_Medium_Placeholder")
    }

    func testLargePlaceholder() {
        assert(state: .placeholder, family: .systemLarge, name: "UpNextWidget_Large_Placeholder")
    }

    private func assert(state: UpNextWidgetState, family: WidgetFamily, name: String) {
        let size = size(for: family)

        for (suffix, theme) in themes {
            UpNextWidgetView(state: state, family: family)
                .appTheme(theme)
                .frame(width: size.width, height: size.height)
                .background(theme.colors.surface)
                .assertSnapshot(
                    layout: .fixed(width: size.width, height: size.height),
                    styles: .light,
                    testName: "\(name)_\(suffix)"
                )
        }
    }

    private var themes: [(String, TvManiacTheme)] {
        [("Light", LightTheme()), ("Dark", DarkTheme())]
    }

    private func size(for family: WidgetFamily) -> CGSize {
        switch family {
        case .systemSmall: CGSize(width: 158, height: 158)
        case .systemMedium: CGSize(width: 338, height: 158)
        default: CGSize(width: 338, height: 354)
        }
    }
}
