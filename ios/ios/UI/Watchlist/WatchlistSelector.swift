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
    @State private var newListName = ""

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
        NavigationStack {
            Form {
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

                if !traktLists.isEmpty {
                    Section {
                        ForEach(traktLists, id: \.id) { list in
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
                                    set: { _ in onToggle(list.id, list.isShowInList) }
                                ))
                                .labelsHidden()
                            }
                        }
                    } header: {
                        Text(String(\.label_watchlist_lists))
                    }
                } else {
                    emptyList
                }

                Section {
                    HStack {
                        TextField("New list name", text: $newListName)
                            .textFieldStyle(.roundedBorder)
                            .onChange(of: newListName) { _, newValue in
                                if newValue.count > 50 {
                                    newListName = String(newValue.prefix(50))
                                }
                            }

                        Button(action: {
                            if !newListName.trimmingCharacters(in: .whitespaces).isEmpty {
                                onCreate(newListName)
                                newListName = ""
                            }
                        }) {
                            Text(String(\.label_watchlist_create))
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(theme.colors.accent)
                        .disabled(newListName.trimmingCharacters(in: .whitespaces).isEmpty)
                    }
                }
            }
            .scrollBounceBehavior(.basedOnSize, axes: .vertical)
            .scrollContentBackground(.hidden)
            .background(theme.colors.background)
            .navigationTitle(Text(String(\.label_watchlist_title)))
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(theme.colors.surface, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    RoundedButton(
                        imageName: "xmark",
                        tintColor: theme.colors.accent,
                        action: { showView.toggle() }
                    )
                }
            }
            .background(theme.colors.background)
        }
        .appTint()
        .appTheme()
        .presentationDetents([.large])
        .presentationDragIndicator(.visible)
        .presentationCornerRadius(theme.shapes.large)
    }

    private var emptyList: some View {
        Section {
            VStack {
                Text(String(\.label_watchlist_empty_list))
                    .textStyle(theme.typography.bodySmall)
                    .multilineTextAlignment(.center)
            }
            .frame(maxWidth: .infinity)
        }
        .listRowInsets(EdgeInsets())
        .listRowBackground(Color.clear)
    }
}
