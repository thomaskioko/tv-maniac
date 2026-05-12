import SwiftUI

public struct ProgressScreen<UpNextContent: View, CalendarContent: View>: View {
    public struct State: Equatable {
        public let title: String
        public let isLoading: Bool
        public let selectedPage: Int
        public let upNextTabTitle: String
        public let calendarTabTitle: String

        public init(
            title: String,
            isLoading: Bool,
            selectedPage: Int,
            upNextTabTitle: String,
            calendarTabTitle: String
        ) {
            self.title = title
            self.isLoading = isLoading
            self.selectedPage = selectedPage
            self.upNextTabTitle = upNextTabTitle
            self.calendarTabTitle = calendarTabTitle
        }
    }

    @Theme private var theme

    private let state: State
    private let onPageChanged: (Int) -> Void
    @ViewBuilder private let upNextContent: () -> UpNextContent
    @ViewBuilder private let calendarContent: () -> CalendarContent

    public init(
        state: State,
        onPageChanged: @escaping (Int) -> Void,
        @ViewBuilder upNextContent: @escaping () -> UpNextContent,
        @ViewBuilder calendarContent: @escaping () -> CalendarContent
    ) {
        self.state = state
        self.onPageChanged = onPageChanged
        self.upNextContent = upNextContent
        self.calendarContent = calendarContent
    }

    public var body: some View {
        VStack(spacing: 0) {
            pagePicker

            TabView(selection: Binding(
                get: { state.selectedPage },
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
            Text(state.title)
                .textStyle(theme.typography.titleMedium)
                .lineLimit(1)
                .foregroundColor(theme.colors.onSurface)

            if state.isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                    .scaleEffect(0.7)
            }
        }
    }

    private var pagePicker: some View {
        Picker("", selection: Binding(
            get: { state.selectedPage },
            set: { onPageChanged($0) }
        )) {
            Text(state.upNextTabTitle).tag(0)
            Text(state.calendarTabTitle).tag(1)
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
                state: ProgressScreen.State(
                    title: "Progress",
                    isLoading: false,
                    selectedPage: 0,
                    upNextTabTitle: "Up Next",
                    calendarTabTitle: "Calendar"
                ),
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
                state: ProgressScreen.State(
                    title: "Progress",
                    isLoading: true,
                    selectedPage: 0,
                    upNextTabTitle: "Up Next",
                    calendarTabTitle: "Calendar"
                ),
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
