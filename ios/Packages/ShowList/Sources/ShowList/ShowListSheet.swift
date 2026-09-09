import Components
import DesignSystem
import SwiftUI
import TvManiacKit

public struct ShowListSheet: View {
    @Environment(\.appTheme) private var theme

    private let state: State
    private let onDismiss: () -> Void
    private let onShowCreateListField: () -> Void
    private let onToggle: (Int64, Bool) -> Void
    private let onCreateListNameChanged: (String) -> Void
    private let onCreateListSubmitted: () -> Void

    public init(
        state: State,
        onDismiss: @escaping () -> Void = {},
        onShowCreateListField: @escaping () -> Void = {},
        onToggle: @escaping (Int64, Bool) -> Void = { _, _ in },
        onCreateListNameChanged: @escaping (String) -> Void = { _ in },
        onCreateListSubmitted: @escaping () -> Void = {}
    ) {
        self.state = state
        self.onDismiss = onDismiss
        self.onShowCreateListField = onShowCreateListField
        self.onToggle = onToggle
        self.onCreateListNameChanged = onCreateListNameChanged
        self.onCreateListSubmitted = onCreateListSubmitted
    }

    public var body: some View {
        NavigationStack {
            loggedInContent
                .background(.appBackground)
                .navigationTitle(state.labels.sheetTitle)
                .navigationBarTitleDisplayMode(.inline)
                .toolbarBackground(.appSurface, for: .navigationBar)
                .toolbarBackground(.visible, for: .navigationBar)
                .toolbar {
                    ToolbarItem(placement: .topBarLeading) {
                        Button(action: onDismiss) {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundStyle(.appAccent)
                        }
                    }
                    ToolbarItem(placement: .topBarTrailing) {
                        if !state.showCreateListField {
                            Button(action: onShowCreateListField) {
                                Image(systemName: "plus")
                                    .foregroundStyle(.appOnAccent)
                                    .frame(width: 28, height: 28)
                                    .background(.appAccent)
                                    .clipShape(Circle())
                            }
                        }
                    }
                }
        }
        .presentationDetents([.large])
        .presentationDragIndicator(.visible)
        .presentationCornerRadius(16)
        .appTint()
        .appTheme()
    }

    private var loggedInContent: some View {
        Form {
            if state.isLoading {
                loadingSection
            } else if state.lists.isEmpty {
                emptySection
            } else {
                listsSection
            }
            if state.showCreateListField {
                createSection
            }
        }
        .scrollBounceBehavior(.basedOnSize, axes: .vertical)
        .scrollContentBackground(.hidden)
    }

    private var loadingSection: some View {
        Section {
            HStack {
                Spacer()
                ProgressView()
                    .progressViewStyle(.circular)
                    .tint(theme.colors.accent)
                    .padding(.vertical, 16)
                Spacer()
            }
        }
        .listRowBackground(Color.clear)
    }

    private var listsSection: some View {
        Section {
            ForEach(state.lists, id: \.id) { list in
                listRow(for: list)
            }
        } header: {
            Text(state.labels.listsHeaderText)
        }
    }

    private func listRow(for list: ShowListItem) -> some View {
        HStack {
            VStack(alignment: .leading) {
                Text(list.name)
                    .textStyle(theme.typography.bodyMedium)
                Text(list.showCountText)
                    .foregroundStyle(.appOnSurfaceVariant)
                    .textStyle(theme.typography.bodySmall)
            }
            Spacer()
            if list.isToggling {
                ProgressView()
                    .progressViewStyle(.circular)
                    .scaleEffect(0.8)
                    .tint(theme.colors.accent)
            } else {
                Toggle("", isOn: Binding(
                    get: { list.isShowInList },
                    set: { _ in onToggle(list.id, list.isShowInList) }
                ))
                .labelsHidden()
                .tint(theme.colors.accent)
            }
        }
        .padding(.vertical, 4)
        .listRowBackground(theme.colors.surfaceVariant.opacity(0.5))
    }

    private var emptySection: some View {
        Section {
            VStack {
                Text(state.labels.emptyListText)
                    .textStyle(theme.typography.bodyMedium)
                    .multilineTextAlignment(.center)
                    .foregroundStyle(.appOnSurfaceVariant)
            }
            .frame(maxWidth: .infinity)
        }
        .listRowBackground(Color.clear)
    }

    private var createSection: some View {
        Section {
            HStack(spacing: 8) {
                TextField(state.labels.createListPlaceholder, text: Binding(
                    get: { state.createListName },
                    set: { newValue in
                        if newValue.count <= 50 {
                            onCreateListNameChanged(newValue)
                        }
                    }
                ))
                .disabled(state.isCreatingList)
                .padding(.horizontal, 8)
                .padding(.vertical, 6)
                .background(.appSurface)
                .clipShape(RoundedRectangle(cornerRadius: 8))
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(.appOutline.opacity(0.3), lineWidth: 1)
                )

                Button(action: onCreateListSubmitted) {
                    if state.isCreatingList {
                        ProgressView()
                            .progressViewStyle(.circular)
                            .scaleEffect(0.8)
                    } else {
                        Text(state.labels.createListDoneText)
                    }
                }
                .buttonStyle(.borderedProminent)
                .tint(theme.colors.accent)
                .disabled(
                    state.createListName.trimmingCharacters(in: .whitespaces).isEmpty ||
                        state.isCreatingList
                )
            }
            .listRowBackground(Color.clear)
            .listRowSeparator(.hidden)
        }
    }
}

public extension ShowListSheet {
    struct State: Equatable {
        public let isLoading: Bool
        public let lists: [ShowListItem]
        public let showCreateListField: Bool
        public let isCreatingList: Bool
        public let createListName: String
        public let labels: Labels

        public init(
            isLoading: Bool = true,
            lists: [ShowListItem] = [],
            showCreateListField: Bool = false,
            isCreatingList: Bool = false,
            createListName: String = "",
            labels: Labels = Labels()
        ) {
            self.isLoading = isLoading
            self.lists = lists
            self.showCreateListField = showCreateListField
            self.isCreatingList = isCreatingList
            self.createListName = createListName
            self.labels = labels
        }
    }

    struct Labels: Equatable {
        public let sheetTitle: String
        public let createListDoneText: String
        public let createListPlaceholder: String
        public let emptyListText: String
        public let listsHeaderText: String

        public init(
            sheetTitle: String = "",
            createListDoneText: String = "",
            createListPlaceholder: String = "",
            emptyListText: String = "",
            listsHeaderText: String = ""
        ) {
            self.sheetTitle = sheetTitle
            self.createListDoneText = createListDoneText
            self.createListPlaceholder = createListPlaceholder
            self.emptyListText = emptyListText
            self.listsHeaderText = listsHeaderText
        }
    }
}

public struct ShowListItem: Equatable, Identifiable {
    public let id: Int64
    public let name: String
    public let showCountText: String
    public let isShowInList: Bool
    public let isToggling: Bool

    public init(
        id: Int64,
        name: String,
        showCountText: String,
        isShowInList: Bool,
        isToggling: Bool = false
    ) {
        self.id = id
        self.name = name
        self.showCountText = showCountText
        self.isShowInList = isShowInList
        self.isToggling = isToggling
    }
}
