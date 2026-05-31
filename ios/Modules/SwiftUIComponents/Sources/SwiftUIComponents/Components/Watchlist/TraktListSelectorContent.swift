import Components
import DesignSystem
import Models
import SwiftUI

public struct TraktListSelectorContent: View {
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
        public let newListPlaceholder: String
        public let listsHeaderText: String

        public init(
            title: String,
            posterUrl: String?,
            traktLists: [SwiftTraktListItem],
            showCreateField: Bool = false,
            isCreatingList: Bool = false,
            createListName: String = "",
            sheetTitle: String = "Save to List",
            createButtonText: String = "Create a List",
            doneButtonText: String = "Done",
            emptyListText: String = "You don't have any lists yet.",
            newListPlaceholder: String = "New list name",
            listsHeaderText: String = "Your Lists"
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
            self.newListPlaceholder = newListPlaceholder
            self.listsHeaderText = listsHeaderText
        }
    }

    @Environment(\.appTheme) private var theme
    private let state: State
    private let onToggle: (Int64, Bool) -> Void
    private let onShowCreateField: () -> Void
    private let onDismissCreateField: () -> Void
    private let onCreateListNameChanged: (String) -> Void
    private let onCreateSubmitted: () -> Void
    private let onDismiss: () -> Void

    public init(
        state: State,
        onToggle: @escaping (Int64, Bool) -> Void,
        onShowCreateField: @escaping () -> Void,
        onDismissCreateField: @escaping () -> Void,
        onCreateListNameChanged: @escaping (String) -> Void,
        onCreateSubmitted: @escaping () -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.state = state
        self.onToggle = onToggle
        self.onShowCreateField = onShowCreateField
        self.onDismissCreateField = onDismissCreateField
        self.onCreateListNameChanged = onCreateListNameChanged
        self.onCreateSubmitted = onCreateSubmitted
        self.onDismiss = onDismiss
    }

    public var body: some View {
        NavigationStack {
            Form {
                posterSection

                if !state.traktLists.isEmpty {
                    listsSection
                } else {
                    emptySection
                }

                createSection
            }
            .scrollBounceBehavior(.basedOnSize, axes: .vertical)
            .scrollContentBackground(.hidden)
            .background(.appBackground)
            .navigationTitle(state.sheetTitle)
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(.appSurface, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button(action: onDismiss) {
                        Image(systemName: "xmark.circle.fill")
                            .textStyle(theme.typography.titleLarge)
                            .foregroundStyle(.appAccent)
                    }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    if !state.showCreateField {
                        Button(action: onShowCreateField) {
                            Image(systemName: "plus")
                                .textStyle(theme.typography.labelLarge)
                                .foregroundStyle(.appOnAccent)
                                .frame(width: 28, height: 28)
                                .background(.appAccent)
                                .clipShape(Circle())
                        }
                    }
                }
            }
            .background(.appBackground)
        }
    }

    private var posterSection: some View {
        Section {
            VStack(alignment: .center) {
                HStack(alignment: .center) {
                    PosterItemView(
                        title: state.title,
                        posterUrl: state.posterUrl,
                        posterWidth: 150,
                        posterHeight: 220
                    )
                    .frame(width: 150, height: 220)
                    .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large, style: .continuous))
                    .appShadow(theme.shadows.large)
                }
                .frame(maxWidth: .infinity)

                Spacer().frame(height: 16)

                Text(state.title)
                    .textStyle(theme.typography.titleMedium)
                    .multilineTextAlignment(.center)
            }
        }
        .listRowInsets(EdgeInsets())
        .listRowBackground(Color.clear)
    }

    private var listsSection: some View {
        Section {
            ForEach(state.traktLists) { list in
                HStack {
                    VStack(alignment: .leading) {
                        Text(list.name)
                            .textStyle(theme.typography.bodyMedium)
                        Text(list.showCountText)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundStyle(.appOnSurfaceVariant)
                    }
                    Spacer()
                    Toggle("", isOn: Binding(
                        get: { list.isShowInList },
                        set: { _ in onToggle(list.listId, list.isShowInList) }
                    ))
                    .labelsHidden()
                    .tint(theme.colors.secondary)
                }
                .padding(.horizontal, theme.spacing.medium)
                .padding(.vertical, theme.spacing.small)
                .background(.appSurfaceVariant.opacity(0.5))
                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium, style: .continuous))
                .listRowInsets(EdgeInsets(
                    top: theme.spacing.xxSmall,
                    leading: theme.spacing.xSmall,
                    bottom: theme.spacing.xxSmall,
                    trailing: theme.spacing.xSmall
                ))
                .listRowBackground(Color.clear)
                .listRowSeparator(.hidden)
            }
        } header: {
            Text(state.listsHeaderText)
        }
    }

    private var emptySection: some View {
        Section {
            VStack {
                Text(state.emptyListText)
                    .textStyle(theme.typography.bodySmall)
                    .multilineTextAlignment(.center)
            }
            .frame(maxWidth: .infinity)
        }
        .listRowInsets(EdgeInsets())
        .listRowBackground(Color.clear)
    }

    @ViewBuilder
    private var createSection: some View {
        if state.showCreateField {
            Section {
                HStack(spacing: theme.spacing.xSmall) {
                    TextField(state.newListPlaceholder, text: Binding(
                        get: { state.createListName },
                        set: { newValue in
                            if newValue.count <= 50 {
                                onCreateListNameChanged(newValue)
                            }
                        }
                    ))
                    .textStyle(theme.typography.bodyMedium)
                    .disabled(state.isCreatingList)
                    .padding(.horizontal, theme.spacing.small)
                    .padding(.vertical, theme.spacing.xSmall)
                    .background(.appSurface)
                    .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
                    .overlay(
                        RoundedRectangle(cornerRadius: theme.shapes.medium)
                            .stroke(.appOutline.opacity(0.3), lineWidth: 1)
                    )

                    Button(action: onCreateSubmitted) {
                        if state.isCreatingList {
                            ProgressView()
                                .progressViewStyle(.circular)
                                .scaleEffect(0.8)
                        } else {
                            Text(state.doneButtonText)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(theme.colors.accent)
                    .disabled(state.createListName.trimmingCharacters(in: .whitespaces).isEmpty || state.isCreatingList)
                }
                .listRowInsets(EdgeInsets(top: 8, leading: 8, bottom: 8, trailing: 8))
                .listRowBackground(Color.clear)
                .listRowSeparator(.hidden)
            }
        }
    }
}
