import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct FeatureFlagsView: View {
    private let presenter: FeatureFlagsPresenter
    @StateValue private var uiState: FeatureFlagsState

    init(presenter: FeatureFlagsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    var body: some View {
        FeatureFlagsScreen(
            state: uiState.toScreenState(),
            onBack: { presenter.dispatch(action: BackClicked_()) },
            onSearchQueryChanged: { presenter.dispatch(action: SearchQueryChanged(query: $0)) },
            onResetAll: { presenter.dispatch(action: ClearAllLocals()) },
            onForceRefresh: { presenter.dispatch(action: ForceRefresh()) },
            onToggle: { id, value in
                if let flag = FeatureFlag.entries.first(where: { $0.key == id }) {
                    presenter.dispatch(action: ToggleFlag(flag: flag, value: value))
                }
            },
            onResetItem: { id in
                if let flag = FeatureFlag.entries.first(where: { $0.key == id }) {
                    presenter.dispatch(action: ClearLocal(flag: flag))
                }
            },
            onSortChanged: { id in
                if let descriptor = FeatureFlagSortDescriptor.entries.first(where: { $0.name == id }) {
                    presenter.dispatch(action: SortChanged(sort: descriptor))
                }
            },
            onDirectionToggled: { presenter.dispatch(action: DirectionToggled()) },
            onGroupByTypeToggled: { presenter.dispatch(action: GroupByTypeToggled()) }
        )
    }
}

private extension FeatureFlagsState {
    func toScreenState() -> FeatureFlagsScreen.State {
        FeatureFlagsScreen.State(
            title: title,
            searchQuery: searchQuery,
            searchPlaceholder: searchHint,
            resetAllTitle: resetAllTitle,
            resetAllSubtitle: resetAllSubtitle,
            forceRefreshTitle: forceRefreshTitle,
            forceRefreshSubtitle: forceRefreshSubtitle,
            resetButtonLabel: resetButtonLabel,
            emptyMessage: emptyResults,
            moreActionsLabel: moreActionsLabel,
            groupByTypeLabel: groupByTypeLabel,
            noGroupingLabel: noGroupingLabel,
            sortAscendingLabel: sortAscendingLabel,
            sortDescendingLabel: sortDescendingLabel,
            sortOptions: FeatureFlagSortDescriptor.entries.map { descriptor in
                FeatureFlagsScreen.SortOption(id: descriptor.name, label: descriptor.label)
            },
            activeSortId: sort.name,
            ascending: ascending,
            groupByType: groupByType,
            items: items.map { item in
                FeatureFlagsScreen.Item(
                    id: item.flag.key,
                    title: item.title,
                    description: item.description_,
                    source: item.source,
                    isOn: item.value,
                    isLocal: item.isLocal
                )
            }
        )
    }
}
