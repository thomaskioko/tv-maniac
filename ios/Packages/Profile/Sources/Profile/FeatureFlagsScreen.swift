import Components
import DesignSystem
import Models
import SwiftUI

public struct FeatureFlagsScreen: View {
    public struct State: Equatable {
        public let title: String
        public let searchQuery: String
        public let searchPlaceholder: String
        public let resetAllTitle: String
        public let resetAllSubtitle: String
        public let forceRefreshTitle: String
        public let forceRefreshSubtitle: String
        public let resetButtonLabel: String
        public let emptyMessage: String
        public let moreActionsLabel: String
        public let groupByTypeLabel: String
        public let noGroupingLabel: String
        public let sortAscendingLabel: String
        public let sortDescendingLabel: String
        public let sortOptions: [SortOption]
        public let activeSortId: String
        public let ascending: Bool
        public let groupByType: Bool
        public let items: [Item]

        public init(
            title: String,
            searchQuery: String,
            searchPlaceholder: String,
            resetAllTitle: String,
            resetAllSubtitle: String,
            forceRefreshTitle: String,
            forceRefreshSubtitle: String,
            resetButtonLabel: String,
            emptyMessage: String,
            moreActionsLabel: String,
            groupByTypeLabel: String,
            noGroupingLabel: String,
            sortAscendingLabel: String,
            sortDescendingLabel: String,
            sortOptions: [SortOption],
            activeSortId: String,
            ascending: Bool,
            groupByType: Bool,
            items: [Item]
        ) {
            self.title = title
            self.searchQuery = searchQuery
            self.searchPlaceholder = searchPlaceholder
            self.resetAllTitle = resetAllTitle
            self.resetAllSubtitle = resetAllSubtitle
            self.forceRefreshTitle = forceRefreshTitle
            self.forceRefreshSubtitle = forceRefreshSubtitle
            self.resetButtonLabel = resetButtonLabel
            self.emptyMessage = emptyMessage
            self.moreActionsLabel = moreActionsLabel
            self.groupByTypeLabel = groupByTypeLabel
            self.noGroupingLabel = noGroupingLabel
            self.sortAscendingLabel = sortAscendingLabel
            self.sortDescendingLabel = sortDescendingLabel
            self.sortOptions = sortOptions
            self.activeSortId = activeSortId
            self.ascending = ascending
            self.groupByType = groupByType
            self.items = items
        }
    }

    public struct Item: Identifiable, Equatable {
        public let id: String
        public let title: String
        public let description: String
        public let source: String
        public let isOn: Bool
        public let isLocal: Bool

        public init(
            id: String,
            title: String,
            description: String,
            source: String,
            isOn: Bool,
            isLocal: Bool
        ) {
            self.id = id
            self.title = title
            self.description = description
            self.source = source
            self.isOn = isOn
            self.isLocal = isLocal
        }
    }

    public struct SortOption: Identifiable, Equatable {
        public let id: String
        public let label: String

        public init(id: String, label: String) {
            self.id = id
            self.label = label
        }
    }

    @Environment(\.appTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let state: State
    private let onBack: () -> Void
    private let onSearchQueryChanged: (String) -> Void
    private let onResetAll: () -> Void
    private let onForceRefresh: () -> Void
    private let onToggle: (String, Bool) -> Void
    private let onResetItem: (String) -> Void
    private let onSortChanged: (String) -> Void
    private let onDirectionToggled: () -> Void
    private let onGroupByTypeToggled: () -> Void

    public init(
        state: State,
        onBack: @escaping () -> Void,
        onSearchQueryChanged: @escaping (String) -> Void,
        onResetAll: @escaping () -> Void,
        onForceRefresh: @escaping () -> Void,
        onToggle: @escaping (String, Bool) -> Void,
        onResetItem: @escaping (String) -> Void,
        onSortChanged: @escaping (String) -> Void,
        onDirectionToggled: @escaping () -> Void,
        onGroupByTypeToggled: @escaping () -> Void
    ) {
        self.state = state
        self.onBack = onBack
        self.onSearchQueryChanged = onSearchQueryChanged
        self.onResetAll = onResetAll
        self.onForceRefresh = onForceRefresh
        self.onToggle = onToggle
        self.onResetItem = onResetItem
        self.onSortChanged = onSortChanged
        self.onDirectionToggled = onDirectionToggled
        self.onGroupByTypeToggled = onGroupByTypeToggled
    }

    public var body: some View {
        List {
            Section {
                searchField
                    .listRowInsets(
                        EdgeInsets(
                            top: theme.spacing.small,
                            leading: theme.spacing.medium,
                            bottom: theme.spacing.small,
                            trailing: theme.spacing.medium
                        )
                    )
                    .listRowSeparator(.hidden)
                actionRow(
                    icon: "arrow.counterclockwise",
                    title: state.resetAllTitle,
                    subtitle: state.resetAllSubtitle,
                    onTap: onResetAll
                )
                .listRowInsets(rowInsets)
                actionRow(
                    icon: "arrow.clockwise",
                    title: state.forceRefreshTitle,
                    subtitle: state.forceRefreshSubtitle,
                    onTap: onForceRefresh
                )
                .listRowInsets(rowInsets)
                .listRowSeparator(state.items.isEmpty ? .hidden : .visible, edges: .bottom)
            }
            .listRowBackground(theme.colors.background)
            .listRowSeparatorTint(theme.colors.onSurface.opacity(0.12))

            if state.items.isEmpty {
                emptyRow
                    .listRowBackground(theme.colors.background)
                    .listRowSeparator(.hidden)
            } else {
                Section {
                    ForEach(Array(state.items.enumerated()), id: \.element.id) { index, item in
                        itemRow(for: item)
                            .listRowInsets(rowInsets)
                            .listRowSeparator(
                                index == state.items.count - 1 ? .hidden : .visible,
                                edges: .bottom
                            )
                    }
                }
                .listRowBackground(theme.colors.background)
                .listRowSeparatorTint(theme.colors.onSurface.opacity(0.12))
            }
        }
        .listStyle(.plain)
        .contentMargins(.top, toolbarInset + theme.spacing.medium)
        .scrollContentBackground(.hidden)
        .appScreen()
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(toolbar, alignment: .top)
        .edgesIgnoringSafeArea(.top)
    }

    // MARK: - Toolbar

    private var toolbar: some View {
        GlassToolbar(
            title: state.title,
            opacity: 1.0,
            leadingIcon: {
                GlassButton(icon: "chevron.left", action: onBack)
            },
            trailingIcon: {
                overflowMenu
            }
        )
    }

    private var overflowMenu: some View {
        Menu {
            Button(action: {
                if !state.groupByType { onGroupByTypeToggled() }
            }) {
                checkLabel(state.groupByTypeLabel, checked: state.groupByType)
            }
            Button(action: {
                if state.groupByType { onGroupByTypeToggled() }
            }) {
                checkLabel(state.noGroupingLabel, checked: !state.groupByType)
            }
            Divider()
            ForEach(state.sortOptions) { option in
                Button(action: { onSortChanged(option.id) }) {
                    checkLabel(option.label, checked: option.id == state.activeSortId)
                }
            }
            Divider()
            Button(action: {
                if !state.ascending { onDirectionToggled() }
            }) {
                checkLabel(state.sortAscendingLabel, checked: state.ascending)
            }
            Button(action: {
                if state.ascending { onDirectionToggled() }
            }) {
                checkLabel(state.sortDescendingLabel, checked: !state.ascending)
            }
        } label: {
            ZStack {
                Circle()
                    .fill(theme.colors.scrim.opacity(colorScheme == .dark ? 0.5 : 0.3))
                    .frame(width: 44, height: 44)
                    .overlay(
                        Circle()
                            .strokeBorder(theme.colors.onScrim.opacity(0.15), lineWidth: 1)
                    )
                    .appShadow(theme.shadows.medium)

                Image(systemName: "line.3.horizontal.decrease")
                    .textStyle(theme.typography.titleMedium)
                    .foregroundStyle(.appOnPrimary)
            }
            .frame(width: 44, height: 44)
            .accessibilityLabel(state.moreActionsLabel)
        }
    }

    @ViewBuilder
    private func checkLabel(_ label: String, checked: Bool) -> some View {
        if checked {
            Label(label, systemImage: "checkmark")
        } else {
            Text(label)
        }
    }

    // MARK: - Rows

    private var searchField: some View {
        HStack(spacing: theme.spacing.small) {
            Image(systemName: "magnifyingglass")
                .foregroundStyle(.appOnSurfaceVariant)
            TextField(
                state.searchPlaceholder,
                text: Binding(
                    get: { state.searchQuery },
                    set: { onSearchQueryChanged($0) }
                )
            )
            .textInputAutocapitalization(.never)
            .autocorrectionDisabled()
            .textStyle(theme.typography.bodyMedium)
            if !state.searchQuery.isEmpty {
                Button(action: { onSearchQueryChanged("") }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundStyle(.appOnSurfaceVariant)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, theme.spacing.medium)
        .padding(.vertical, theme.spacing.small)
        .background(theme.colors.surface)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private func actionRow(
        icon: String,
        title: String,
        subtitle: String,
        onTap: @escaping () -> Void
    ) -> some View {
        HStack(spacing: theme.spacing.medium) {
            Image(systemName: icon)
                .foregroundStyle(.appSecondary)
                .frame(width: theme.spacing.large, height: theme.spacing.large)
            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundStyle(.appOnSurface)
                Text(subtitle)
                    .textStyle(theme.typography.bodySmall)
                    .foregroundStyle(.appOnSurfaceVariant)
            }
            Spacer()
        }
        .padding(.vertical, theme.spacing.small)
        .contentShape(Rectangle())
        .onTapGesture { onTap() }
    }

    private func itemRow(for item: Item) -> some View {
        HStack(alignment: .center, spacing: theme.spacing.medium) {
            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                HStack(alignment: .lastTextBaseline, spacing: theme.spacing.small) {
                    Text(item.title)
                        .textStyle(theme.typography.titleMedium)
                        .foregroundStyle(.appOnSurface)
                    Text(item.source)
                        .textStyle(theme.typography.labelSmall)
                        .foregroundStyle(.appOnSurfaceVariant)
                }
                Text(item.description)
                    .textStyle(theme.typography.bodySmall)
                    .foregroundStyle(.appOnSurfaceVariant)
            }
            Spacer()
            if item.isLocal {
                Button(action: { onResetItem(item.id) }) {
                    Text(state.resetButtonLabel)
                        .textStyle(theme.typography.labelMedium)
                        .foregroundStyle(.appSecondary)
                }
                .buttonStyle(.plain)
            }
            Toggle(
                "",
                isOn: Binding(
                    get: { item.isOn },
                    set: { onToggle(item.id, $0) }
                )
            )
            .labelsHidden()
            .tint(theme.colors.secondary)
        }
        .padding(.vertical, theme.spacing.small)
    }

    private var emptyRow: some View {
        Text(state.emptyMessage)
            .textStyle(theme.typography.bodyMedium)
            .foregroundStyle(.appOnSurfaceVariant)
            .frame(maxWidth: .infinity)
            .padding(theme.spacing.medium)
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }

    private var rowInsets: EdgeInsets {
        EdgeInsets(
            top: 4,
            leading: theme.spacing.medium,
            bottom: 4,
            trailing: theme.spacing.medium
        )
    }
}

#Preview("Feature Flags Screen") {
    FeatureFlagsScreen(
        state: FeatureFlagsScreen.State(
            title: "Feature Flags",
            searchQuery: "",
            searchPlaceholder: "Search flags",
            resetAllTitle: "Reset all",
            resetAllSubtitle: "Clear feature flags or reset to default",
            forceRefreshTitle: "Force refresh",
            forceRefreshSubtitle: "Fetch latest values from Remote Config",
            resetButtonLabel: "Reset",
            emptyMessage: "No flags match this query",
            moreActionsLabel: "More actions",
            groupByTypeLabel: "Group by type",
            noGroupingLabel: "No grouping",
            sortAscendingLabel: "Sort ascending",
            sortDescendingLabel: "Sort descending",
            sortOptions: [
                FeatureFlagsScreen.SortOption(id: "Title", label: "Title"),
                FeatureFlagsScreen.SortOption(id: "Key", label: "Key"),
                FeatureFlagsScreen.SortOption(id: "Date", label: "Date Added"),
            ],
            activeSortId: "Date",
            ascending: false,
            groupByType: false,
            items: [
                FeatureFlagsScreen.Item(
                    id: "simkl_login_enabled",
                    title: "Simkl Login",
                    description: "Show the Simkl login entry point on the settings screen.",
                    source: "Firebase",
                    isOn: false,
                    isLocal: false
                ),
            ]
        ),
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
    .preferredColorScheme(.dark)
}
