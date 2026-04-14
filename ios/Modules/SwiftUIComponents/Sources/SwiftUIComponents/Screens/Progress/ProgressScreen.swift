import SwiftUI

public struct ProgressScreen<UpNextContent: View, CalendarContent: View>: View {
    @Theme private var theme

    private let title: String
    private let isLoading: Bool
    private let selectedPage: Int
    private let upNextTabTitle: String
    private let calendarTabTitle: String
    private let onPageChanged: (Int) -> Void
    @ViewBuilder private let upNextContent: () -> UpNextContent
    @ViewBuilder private let calendarContent: () -> CalendarContent

    public init(
        title: String,
        isLoading: Bool,
        selectedPage: Int,
        upNextTabTitle: String,
        calendarTabTitle: String,
        onPageChanged: @escaping (Int) -> Void,
        @ViewBuilder upNextContent: @escaping () -> UpNextContent,
        @ViewBuilder calendarContent: @escaping () -> CalendarContent
    ) {
        self.title = title
        self.isLoading = isLoading
        self.selectedPage = selectedPage
        self.upNextTabTitle = upNextTabTitle
        self.calendarTabTitle = calendarTabTitle
        self.onPageChanged = onPageChanged
        self.upNextContent = upNextContent
        self.calendarContent = calendarContent
    }

    public var body: some View {
        VStack(spacing: 0) {
            pagePicker

            TabView(selection: Binding(
                get: { selectedPage },
                set: { onPageChanged($0) }
            )) {
                upNextContent()
                    .tag(0)

                calendarContent()
                    .tag(1)
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
        }
        .background(theme.colors.background.ignoresSafeArea())
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                titleView
            }
        }
        .toolbarBackground(theme.colors.surface, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
    }

    private var titleView: some View {
        HStack(spacing: theme.spacing.xSmall) {
            Text(title)
                .textStyle(theme.typography.titleMedium)
                .lineLimit(1)
                .foregroundColor(theme.colors.onSurface)

            if isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                    .scaleEffect(0.7)
            }
        }
    }

    private var pagePicker: some View {
        Picker("", selection: Binding(
            get: { selectedPage },
            set: { onPageChanged($0) }
        )) {
            Text(upNextTabTitle).tag(0)
            Text(calendarTabTitle).tag(1)
        }
        .pickerStyle(.segmented)
        .padding(.horizontal)
        .padding(.vertical, theme.spacing.xSmall)
    }
}

#Preview("Up Next Selected") {
    ThemedPreview {
        NavigationStack {
            ProgressScreen(
                title: "Progress",
                isLoading: false,
                selectedPage: 0,
                upNextTabTitle: "Up Next",
                calendarTabTitle: "Calendar",
                onPageChanged: { _ in },
                upNextContent: {
                    Text("Up Next Content")
                },
                calendarContent: {
                    Text("Calendar Content")
                }
            )
        }
    }
}

#Preview("Loading") {
    ThemedPreview {
        NavigationStack {
            ProgressScreen(
                title: "Progress",
                isLoading: true,
                selectedPage: 0,
                upNextTabTitle: "Up Next",
                calendarTabTitle: "Calendar",
                onPageChanged: { _ in },
                upNextContent: {
                    Text("Up Next Content")
                },
                calendarContent: {
                    Text("Calendar Content")
                }
            )
        }
    }
}
