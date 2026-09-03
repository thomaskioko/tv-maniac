import DesignSystem
import Models
import SwiftUI
import WidgetKit
import Widgets

struct UpNextEntry: TimelineEntry {
    let date: Date
    let state: UpNextWidgetState
    let themeName: String?
}

struct UpNextProvider: TimelineProvider {
    func placeholder(in _: Context) -> UpNextEntry {
        UpNextEntry(date: Date(), state: .placeholder, themeName: nil)
    }

    func getSnapshot(in _: Context, completion: @escaping (UpNextEntry) -> Void) {
        completion(currentEntry())
    }

    func getTimeline(in _: Context, completion: @escaping (Timeline<UpNextEntry>) -> Void) {
        let entry = currentEntry()
        let nextRefresh = Calendar.current.date(byAdding: .hour, value: 1, to: entry.date) ?? entry.date
        completion(Timeline(entries: [entry], policy: .after(nextRefresh)))
    }

    private func currentEntry() -> UpNextEntry {
        let snapshot = UpNextSnapshotReader.read()
        let state = UpNextSnapshotReader.state(
            from: snapshot,
            emptyMessage: String(localized: "widget_empty_watchlist"),
            seasonEpisodeLabel: { season, episode in
                String(
                    format: String(localized: "widget_season_episode"),
                    String(format: "%02d", season),
                    String(format: "%02d", episode)
                )
            },
            destination: { entry in
                URL(string: "tvmaniac://episode/\(entry.tmdbId)/\(entry.seasonNumber)/\(entry.episodeNumber)")
            }
        )
        return UpNextEntry(date: Date(), state: state, themeName: snapshot?.themeName)
    }
}

struct UpNextWidgetEntryView: View {
    @Environment(\.widgetFamily) private var family
    @Environment(\.colorScheme) private var colorScheme

    let entry: UpNextEntry

    var body: some View {
        UpNextWidgetView(state: entry.state, family: family)
            .appTheme(WidgetTheme.named(entry.themeName, colorScheme: colorScheme))
            .environment(\.colorScheme, resolvedColorScheme)
            .widgetURL(family == .systemSmall ? firstDestination : nil)
    }

    private var resolvedColorScheme: ColorScheme {
        guard let prefersDark = WidgetTheme.prefersDarkAppearance(entry.themeName) else { return colorScheme }
        return prefersDark ? .dark : .light
    }

    private var firstDestination: URL? {
        guard case let .content(items, _) = entry.state else { return nil }
        return items.first?.destination
    }
}

struct UpNextWidget: Widget {
    private let kind = "TvManiacUpNextWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: UpNextProvider()) { entry in
            UpNextWidgetEntryView(entry: entry)
        }
        .configurationDisplayName(Text("widget_up_next_name", bundle: .main))
        .description(Text("widget_up_next_description", bundle: .main))
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

private let previewItems: [UpNextItem] = [
    UpNextItem(id: 1396, showName: "Breaking Bad", episodeName: "Pilot", seasonEpisodeLabel: "S01 | E01"),
    UpNextItem(id: 60059, showName: "Better Call Saul", episodeName: "Uno", seasonEpisodeLabel: "S01 | E01"),
    UpNextItem(id: 1399, showName: "Game of Thrones", episodeName: "Winter Is Coming", seasonEpisodeLabel: "S01 | E01"),
    UpNextItem(id: 82856, showName: "The Mandalorian", episodeName: "Chapter 1", seasonEpisodeLabel: "S01 | E01"),
]

#Preview("Small", as: .systemSmall) {
    UpNextWidget()
} timeline: {
    UpNextEntry(date: .now, state: .placeholder, themeName: nil)
    UpNextEntry(date: .now, state: .content(items: previewItems, lastUpdated: nil), themeName: nil)
}

#Preview("Medium", as: .systemMedium) {
    UpNextWidget()
} timeline: {
    UpNextEntry(date: .now, state: .content(items: previewItems, lastUpdated: nil), themeName: nil)
    UpNextEntry(date: .now, state: .empty(message: String(localized: "widget_empty_watchlist")), themeName: nil)
}

#Preview("Large", as: .systemLarge) {
    UpNextWidget()
} timeline: {
    UpNextEntry(date: .now, state: .content(items: previewItems, lastUpdated: nil), themeName: nil)
    UpNextEntry(date: .now, state: .empty(message: String(localized: "widget_empty_watchlist")), themeName: nil)
}
