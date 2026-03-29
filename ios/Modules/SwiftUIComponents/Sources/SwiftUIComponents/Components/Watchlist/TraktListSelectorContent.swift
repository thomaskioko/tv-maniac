import SwiftUI

public struct TraktListSelectorContent: View {
    @Theme private var theme
    private let title: String
    private let posterUrl: String?
    private let traktLists: [SwiftTraktListItem]
    private let showCreateField: Bool
    private let isCreatingList: Bool
    private let createListName: String
    private let emptyListText: String
    private let newListPlaceholder: String
    private let listsHeaderText: String
    private let onToggle: (Int64, Bool) -> Void
    private let onShowCreateField: () -> Void
    private let onDismissCreateField: () -> Void
    private let onCreateListNameChanged: (String) -> Void
    private let onCreateSubmitted: () -> Void
    private let onDismiss: () -> Void

    public init(
        title: String,
        posterUrl: String?,
        traktLists: [SwiftTraktListItem],
        showCreateField: Bool = false,
        isCreatingList: Bool = false,
        createListName: String = "",
        emptyListText: String = "You don't have any lists yet.",
        newListPlaceholder: String = "New list name",
        listsHeaderText: String = "Your Lists",
        onToggle: @escaping (Int64, Bool) -> Void,
        onShowCreateField: @escaping () -> Void,
        onDismissCreateField: @escaping () -> Void,
        onCreateListNameChanged: @escaping (String) -> Void,
        onCreateSubmitted: @escaping () -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.title = title
        self.posterUrl = posterUrl
        self.traktLists = traktLists
        self.showCreateField = showCreateField
        self.isCreatingList = isCreatingList
        self.createListName = createListName
        self.emptyListText = emptyListText
        self.newListPlaceholder = newListPlaceholder
        self.listsHeaderText = listsHeaderText
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

                if !traktLists.isEmpty {
                    listsSection
                } else {
                    emptySection
                }

                createSection
            }
            .scrollBounceBehavior(.basedOnSize, axes: .vertical)
            .scrollContentBackground(.hidden)
            .background(theme.colors.background)
            .navigationTitle("Save to List")
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(theme.colors.surface, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    RoundedButton(
                        imageName: "xmark",
                        tintColor: theme.colors.accent,
                        action: onDismiss
                    )
                }
            }
            .background(theme.colors.background)
        }
    }

    private var posterSection: some View {
        Section {
            VStack(alignment: .center) {
                HStack(alignment: .center) {
                    PosterItemView(
                        title: title,
                        posterUrl: posterUrl,
                        posterWidth: 150,
                        posterHeight: 220
                    )
                    .frame(width: 150, height: 220)
                    .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large, style: .continuous))
                    .shadow(color: Color.black.opacity(0.2), radius: 10, x: 0, y: 10)
                }
                .frame(maxWidth: .infinity)

                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .multilineTextAlignment(.center)
            }
        }
        .listRowInsets(EdgeInsets())
        .listRowBackground(Color.clear)
    }

    private var listsSection: some View {
        Section {
            ForEach(traktLists) { list in
                HStack {
                    VStack(alignment: .leading) {
                        Text(list.name)
                            .textStyle(theme.typography.bodyMedium)
                        Text("\(list.itemCount) items")
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                    }
                    Spacer()
                    Toggle("", isOn: Binding(
                        get: { list.isShowInList },
                        set: { _ in onToggle(list.listId, list.isShowInList) }
                    ))
                    .labelsHidden()
                }
            }
        } header: {
            Text(listsHeaderText)
        }
    }

    private var emptySection: some View {
        Section {
            VStack {
                Text(emptyListText)
                    .textStyle(theme.typography.bodySmall)
                    .multilineTextAlignment(.center)
            }
            .frame(maxWidth: .infinity)
        }
        .listRowInsets(EdgeInsets())
        .listRowBackground(Color.clear)
    }

    private var createSection: some View {
        Section {
            if !showCreateField {
                Button(action: onShowCreateField) {
                    HStack {
                        Image(systemName: "plus.circle.fill")
                        Text("Create List")
                    }
                    .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)
                .tint(theme.colors.accent)
            } else {
                VStack(spacing: theme.spacing.xSmall) {
                    HStack {
                        Spacer()
                        Button(action: onDismissCreateField) {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(theme.colors.onSurfaceVariant)
                        }
                        .buttonStyle(.plain)
                    }

                    HStack {
                        TextField(newListPlaceholder, text: Binding(
                            get: { createListName },
                            set: { newValue in
                                if newValue.count <= 50 {
                                    onCreateListNameChanged(newValue)
                                }
                            }
                        ))
                        .textFieldStyle(.roundedBorder)
                        .disabled(isCreatingList)

                        Button(action: onCreateSubmitted) {
                            if isCreatingList {
                                ProgressView()
                                    .progressViewStyle(.circular)
                                    .scaleEffect(0.8)
                            } else {
                                Text("Done")
                            }
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(theme.colors.accent)
                        .disabled(createListName.trimmingCharacters(in: .whitespaces).isEmpty || isCreatingList)
                    }
                }
            }
        }
    }
}
