import SwiftUI
import SwiftUIComponents
import TvManiacKit

public struct WatchlistSelector: View {
    public struct State {
        public let title: String
        public let posterUrl: String?
        public let traktLists: [SwiftTraktListItem]
        public let showCreateField: Bool
        public let isCreatingList: Bool
        public let createListName: String
        public let sheetTitle: String
        public let createButtonText: String
        public let doneButtonText: String
        public let emptyListText: String
        public let createListPlaceholder: String
        public let listsHeaderText: String

        public init(
            title: String,
            posterUrl: String?,
            traktLists: [SwiftTraktListItem],
            showCreateField: Bool,
            isCreatingList: Bool,
            createListName: String,
            sheetTitle: String,
            createButtonText: String,
            doneButtonText: String,
            emptyListText: String,
            createListPlaceholder: String,
            listsHeaderText: String
        ) {
            self.title = title
            self.posterUrl = posterUrl
            self.traktLists = traktLists
            self.showCreateField = showCreateField
            self.isCreatingList = isCreatingList
            self.createListName = createListName
            self.sheetTitle = sheetTitle
            self.createButtonText = createButtonText
            self.doneButtonText = doneButtonText
            self.emptyListText = emptyListText
            self.createListPlaceholder = createListPlaceholder
            self.listsHeaderText = listsHeaderText
        }
    }

    @SwiftUIComponents.Theme private var theme
    @Binding var showView: Bool
    private let state: State
    private let onToggle: (Int64, Bool) -> Void
    private let onShowCreateField: () -> Void
    private let onDismissCreateField: () -> Void
    private let onCreateListNameChanged: (String) -> Void
    private let onCreateSubmitted: () -> Void

    public init(
        showView: Binding<Bool>,
        state: State,
        onToggle: @escaping (Int64, Bool) -> Void,
        onShowCreateField: @escaping () -> Void,
        onDismissCreateField: @escaping () -> Void,
        onCreateListNameChanged: @escaping (String) -> Void,
        onCreateSubmitted: @escaping () -> Void
    ) {
        _showView = showView
        self.state = state
        self.onToggle = onToggle
        self.onShowCreateField = onShowCreateField
        self.onDismissCreateField = onDismissCreateField
        self.onCreateListNameChanged = onCreateListNameChanged
        self.onCreateSubmitted = onCreateSubmitted
    }

    public var body: some View {
        TraktListSelectorContent(
            state: TraktListSelectorContent.State(
                title: state.title,
                posterUrl: state.posterUrl,
                traktLists: state.traktLists,
                showCreateField: state.showCreateField,
                isCreatingList: state.isCreatingList,
                createListName: state.createListName,
                sheetTitle: state.sheetTitle,
                createButtonText: state.createButtonText,
                doneButtonText: state.doneButtonText,
                emptyListText: state.emptyListText,
                newListPlaceholder: state.createListPlaceholder,
                listsHeaderText: state.listsHeaderText
            ),
            onToggle: onToggle,
            onShowCreateField: onShowCreateField,
            onDismissCreateField: onDismissCreateField,
            onCreateListNameChanged: onCreateListNameChanged,
            onCreateSubmitted: onCreateSubmitted,
            onDismiss: { showView.toggle() }
        )
        .appTint()
        .appTheme()
        .presentationDetents([.large])
        .presentationDragIndicator(.visible)
        .presentationCornerRadius(theme.shapes.large)
    }
}
