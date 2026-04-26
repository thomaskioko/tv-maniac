import SwiftUI
import SwiftUIComponents
import TvManiacKit

public struct WatchlistSelector: View {
    @SwiftUIComponents.Theme private var theme
    @Binding var showView: Bool
    private let title: String
    private let posterUrl: String?
    private let traktLists: [SwiftTraktListItem]
    private let showCreateField: Bool
    private let isCreatingList: Bool
    private let createListName: String
    private let sheetTitle: String
    private let createButtonText: String
    private let doneButtonText: String
    private let emptyListText: String
    private let createListPlaceholder: String
    private let listsHeaderText: String
    private let onToggle: (Int64, Bool) -> Void
    private let onShowCreateField: () -> Void
    private let onDismissCreateField: () -> Void
    private let onCreateListNameChanged: (String) -> Void
    private let onCreateSubmitted: () -> Void

    public init(
        showView: Binding<Bool>,
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
        listsHeaderText: String,
        onToggle: @escaping (Int64, Bool) -> Void,
        onShowCreateField: @escaping () -> Void,
        onDismissCreateField: @escaping () -> Void,
        onCreateListNameChanged: @escaping (String) -> Void,
        onCreateSubmitted: @escaping () -> Void
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
        self.onToggle = onToggle
        self.onShowCreateField = onShowCreateField
        self.onDismissCreateField = onDismissCreateField
        self.onCreateListNameChanged = onCreateListNameChanged
        self.onCreateSubmitted = onCreateSubmitted
        _showView = showView
    }

    public var body: some View {
        TraktListSelectorContent(
            title: title,
            posterUrl: posterUrl,
            traktLists: traktLists,
            showCreateField: showCreateField,
            isCreatingList: isCreatingList,
            createListName: createListName,
            sheetTitle: sheetTitle,
            createButtonText: createButtonText,
            doneButtonText: doneButtonText,
            emptyListText: emptyListText,
            newListPlaceholder: createListPlaceholder,
            listsHeaderText: listsHeaderText,
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
