import SwiftUI
import SwiftUIComponents
import TvManiacKit

public struct WatchlistSelector: View {
    @Theme private var theme
    @Binding var showView: Bool
    private let title: String
    private let posterUrl: String?
    private let traktLists: [TraktListWithMembership]
    private let onToggle: (Int64, Bool) -> Void
    private let onCreate: (String) -> Void

    public init(
        showView: Binding<Bool>,
        title: String,
        posterUrl: String?,
        traktLists: [TraktListWithMembership],
        onToggle: @escaping (Int64, Bool) -> Void,
        onCreate: @escaping (String) -> Void
    ) {
        self.title = title
        self.posterUrl = posterUrl
        self.traktLists = traktLists
        self.onToggle = onToggle
        self.onCreate = onCreate
        _showView = showView
    }

    public var body: some View {
        TraktListSelectorContent(
            title: title,
            posterUrl: posterUrl,
            traktLists: traktLists.map { list in
                SwiftTraktListItem(
                    listId: list.id,
                    slug: list.slug,
                    name: list.name,
                    description: list.description_,
                    itemCount: list.itemCount,
                    isShowInList: list.isShowInList
                )
            },
            onToggle: onToggle,
            onCreate: onCreate,
            onDismiss: { showView.toggle() }
        )
        .appTint()
        .appTheme()
        .presentationDetents([.large])
        .presentationDragIndicator(.visible)
        .presentationCornerRadius(theme.shapes.large)
    }
}
