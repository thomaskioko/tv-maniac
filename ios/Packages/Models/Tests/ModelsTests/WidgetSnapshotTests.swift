import Foundation
@testable import Models
import XCTest

final class WidgetSnapshotTests: XCTestCase {
    private let expected = WidgetSnapshot(
        writtenAtMillis: 1_724_400_000_000,
        entries: [
            WidgetSnapshotEntry(
                tmdbId: 1396,
                showName: "Breaking Bad",
                episodeName: "Pilot",
                seasonNumber: 1,
                episodeNumber: 1,
                posterFileName: "1396.jpg"
            ),
            WidgetSnapshotEntry(
                tmdbId: 60059,
                showName: "Better Call Saul",
                episodeName: "Uno",
                seasonNumber: 1,
                episodeNumber: 1,
                posterFileName: nil
            ),
        ]
    )

    func testDecodesTheFixtureTheKotlinTestWrites() throws {
        let decoded = try JSONDecoder().decode(WidgetSnapshot.self, from: fixtureData())

        XCTAssertEqual(decoded, expected)
    }

    func testDecodesAnEntryWrittenWithoutAPoster() throws {
        let decoded = try JSONDecoder().decode(WidgetSnapshot.self, from: fixtureData())

        XCTAssertNil(decoded.entries.last?.posterFileName)
    }

    func testDecodesADocumentCarryingAFieldItDoesNotKnow() throws {
        let contents = """
        {
          "writtenAtMillis": 1,
          "entries": [],
          "somethingAddedLater": "value"
        }
        """

        let decoded = try JSONDecoder().decode(WidgetSnapshot.self, from: Data(contents.utf8))

        XCTAssertEqual(decoded.entries, [])
    }

    func testRoundTripsWithoutLoss() throws {
        let encoded = try JSONEncoder().encode(expected)

        XCTAssertEqual(try JSONDecoder().decode(WidgetSnapshot.self, from: encoded), expected)
    }

    private func fixtureData() throws -> Data {
        let url = try XCTUnwrap(
            Bundle.module.url(forResource: "widget-snapshot", withExtension: "json"),
            "widget-snapshot.json is missing from the test bundle"
        )
        return try Data(contentsOf: url)
    }
}
