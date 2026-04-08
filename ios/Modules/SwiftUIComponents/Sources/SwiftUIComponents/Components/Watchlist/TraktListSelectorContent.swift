import SwiftUI

public struct TraktListSelectorContent: View {
    @Theme private var theme
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
        sheetTitle: String = "Save to List",
        createButtonText: String = "Create a List",
        doneButtonText: String = "Done",
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
        self.sheetTitle = sheetTitle
        self.createButtonText = createButtonText
        self.doneButtonText = doneButtonText
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
            .navigationTitle(sheetTitle)
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(theme.colors.surface, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button(action: onDismiss) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title2)
                            .foregroundColor(theme.colors.accent)
                    }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    if !showCreateField {
                        Button(action: onShowCreateField) {
                            Image(systemName: "plus")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(theme.colors.onAccent)
                                .frame(width: 28, height: 28)
                                .background(theme.colors.accent)
                                .clipShape(Circle())
                        }
                    }
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

                Spacer().frame(height: 16)

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
                        Text(list.showCountText)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                    }
                    Spacer()
                    Toggle("", isOn: Binding(
                        get: { list.isShowInList },
                        set: { _ in onToggle(list.listId, list.isShowInList) }
                    ))
                    .labelsHidden()
                    .tint(theme.colors.secondary)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .background(theme.colors.surfaceVariant.opacity(0.5))
                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium, style: .continuous))
                .listRowInsets(EdgeInsets(top: 4, leading: 8, bottom: 4, trailing: 8))
                .listRowBackground(Color.clear)
                .listRowSeparator(.hidden)
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

    @ViewBuilder
    private var createSection: some View {
        if showCreateField {
            Section {
                HStack(spacing: 8) {
                    TextField(newListPlaceholder, text: Binding(
                        get: { createListName },
                        set: { newValue in
                            if newValue.count <= 50 {
                                onCreateListNameChanged(newValue)
                            }
                        }
                    ))
                    .textStyle(theme.typography.bodyMedium)
                    .disabled(isCreatingList)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 10)
                    .background(theme.colors.surface)
                    .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
                    .overlay(
                        RoundedRectangle(cornerRadius: theme.shapes.medium)
                            .stroke(theme.colors.outline.opacity(0.3), lineWidth: 1)
                    )

                    Button(action: onCreateSubmitted) {
                        if isCreatingList {
                            ProgressView()
                                .progressViewStyle(.circular)
                                .scaleEffect(0.8)
                        } else {
                            Text(doneButtonText)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(theme.colors.accent)
                    .disabled(createListName.trimmingCharacters(in: .whitespaces).isEmpty || isCreatingList)
                }
                .listRowInsets(EdgeInsets(top: 8, leading: 8, bottom: 8, trailing: 8))
                .listRowBackground(Color.clear)
                .listRowSeparator(.hidden)
            }
        }
    }
}
