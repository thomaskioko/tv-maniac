import SwiftUI
import SwiftUIComponents

public struct WatchlistSelector: View {
    @Theme private var theme
    @Binding var showView: Bool
    private let title: String
    private let posterUrl: String?
    private let customLists: [String] = []

    public init(showView: Binding<Bool>, title: String, posterUrl: String?) {
        self.title = title
        self.posterUrl = posterUrl
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
                            .fontWeight(.semibold)
                            .textStyle(theme.typography.titleMedium)
                            .multilineTextAlignment(.center)
                    }
                }
                .listRowInsets(EdgeInsets())
                .listRowBackground(Color.clear)

                if !customLists.isEmpty {
                    Section {
                        List {
                            // Add Custom list
                        }
                    } header: {
                        Text(String(\.label_watchlist_lists))
                    }
                } else {
                    emptyList
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

                ToolbarItem(placement: .topBarTrailing) {
                    // TODO: Custom list
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
                Text(String(\.label_watchlist_create_custom_list))
                    .textStyle(theme.typography.titleLarge)
                    .fontWeight(.bold)
                    .foregroundColor(theme.colors.onSurface)
                    .multilineTextAlignment(.center)
                    .padding([.horizontal], theme.spacing.xSmall)

                Text(String(\.label_watchlist_empty_list))
                    .textStyle(theme.typography.bodySmall)

                Button(action: {}) {
                    VStack {
                        Image(systemName: "plus.rectangle.on.rectangle.fill")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(height: 24)

                        Text(String(\.label_watchlist_create))
                            .textStyle(theme.typography.bodySmall)
                    }
                    .padding(.vertical, theme.spacing.xxSmall)
                    .frame(width: 120, height: 45)
                }
                .buttonStyle(.borderedProminent)
                .controlSize(.small)
                .tint(theme.colors.accent)
                .buttonBorderShape(.roundedRectangle(radius: theme.shapes.large))
                .padding(.top, theme.spacing.xSmall)
            }
            .frame(maxWidth: .infinity)
        }
        .listRowInsets(EdgeInsets())
        .listRowBackground(Color.clear)
    }
}
