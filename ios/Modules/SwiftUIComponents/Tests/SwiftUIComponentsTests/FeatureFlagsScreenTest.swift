import DesignSystem
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class FeatureFlagsScreenTest: SnapshotTestCase {
    private let sortOptions: [FeatureFlagsScreen.SortOption] = [
        .init(id: "Title", label: "Title"),
        .init(id: "Key", label: "Key"),
        .init(id: "Date", label: "Date Added"),
    ]

    private var defaultState: FeatureFlagsScreen.State {
        makeState(
            items: [
                .init(
                    id: "simkl_login_enabled",
                    title: "Simkl Login",
                    description: "Show the Simkl login entry point on the settings screen.",
                    source: "Firebase",
                    isOn: false,
                    isLocal: false
                ),
            ]
        )
    }

    private var localSourceState: FeatureFlagsScreen.State {
        makeState(
            forceRefreshSubtitle: "Last fetched at 2026-05-18 09:42",
            items: [
                .init(
                    id: "simkl_login_enabled",
                    title: "Simkl Login",
                    description: "Show the Simkl login entry point on the settings screen.",
                    source: "Local",
                    isOn: true,
                    isLocal: true
                ),
            ]
        )
    }

    private var emptyState: FeatureFlagsScreen.State {
        makeState(searchQuery: "missing_flag", items: [])
    }

    func test_FeatureFlagsScreen_DefaultState() {
        renderScreen(state: defaultState)
            .assertSnapshot(layout: .defaultDevice, testName: "FeatureFlagsScreen_DefaultState")
    }

    func test_FeatureFlagsScreen_LocalSourceState() {
        renderScreen(state: localSourceState)
            .assertSnapshot(layout: .defaultDevice, testName: "FeatureFlagsScreen_LocalSourceState")
    }

    func test_FeatureFlagsScreen_EmptyState() {
        renderScreen(state: emptyState)
            .assertSnapshot(layout: .defaultDevice, testName: "FeatureFlagsScreen_EmptyState")
    }

    private func renderScreen(state: FeatureFlagsScreen.State) -> some View {
        FeatureFlagsScreen(
            state: state,
            onBack: {},
            onSearchQueryChanged: { _ in },
            onResetAll: {},
            onForceRefresh: {},
            onToggle: { _, _ in },
            onResetItem: { _ in },
            onSortChanged: { _ in },
            onDirectionToggled: {},
            onGroupByTypeToggled: {}
        )
        .appPreview()
    }

    private func makeState(
        searchQuery: String = "",
        forceRefreshSubtitle: String = "Fetch latest values from Remote Config",
        items: [FeatureFlagsScreen.Item]
    ) -> FeatureFlagsScreen.State {
        FeatureFlagsScreen.State(
            title: "Feature Flags",
            searchQuery: searchQuery,
            searchPlaceholder: "Search flags",
            resetAllTitle: "Reset all",
            resetAllSubtitle: "Clear feature flags or reset to default",
            forceRefreshTitle: "Force refresh",
            forceRefreshSubtitle: forceRefreshSubtitle,
            resetButtonLabel: "Reset",
            emptyMessage: "No flags match this query",
            moreActionsLabel: "More actions",
            groupByTypeLabel: "Group by type",
            noGroupingLabel: "No grouping",
            sortAscendingLabel: "Sort ascending",
            sortDescendingLabel: "Sort descending",
            sortOptions: sortOptions,
            activeSortId: "Date",
            ascending: false,
            groupByType: false,
            items: items
        )
    }
}
