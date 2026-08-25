import SwiftUI
import WidgetKit

struct UpNextEntry: TimelineEntry {
    let date: Date
}

struct UpNextProvider: TimelineProvider {
    func placeholder(in _: Context) -> UpNextEntry {
        UpNextEntry(date: Date())
    }

    func getSnapshot(in _: Context, completion: @escaping (UpNextEntry) -> Void) {
        completion(UpNextEntry(date: Date()))
    }

    func getTimeline(in _: Context, completion: @escaping (Timeline<UpNextEntry>) -> Void) {
        completion(Timeline(entries: [UpNextEntry(date: Date())], policy: .atEnd))
    }
}

struct UpNextWidgetView: View {
    var entry: UpNextProvider.Entry

    var body: some View {
        Text("widget_empty_watchlist", bundle: .main)
            .multilineTextAlignment(.center)
            .containerBackground(.fill.tertiary, for: .widget)
    }
}

struct TvManiacWidget: Widget {
    let kind = "TvManiacWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: UpNextProvider()) { entry in
            UpNextWidgetView(entry: entry)
        }
        .configurationDisplayName(Text("widget_up_next_name", bundle: .main))
        .description(Text("widget_up_next_description", bundle: .main))
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

#Preview(as: .systemSmall) {
    TvManiacWidget()
} timeline: {
    UpNextEntry(date: .now)
}
