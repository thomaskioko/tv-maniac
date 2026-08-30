import Foundation
import Models
import UIKit

public enum UpNextSnapshotReader {
    public static func read(bundle: Bundle = .main) -> WidgetSnapshot? {
        guard let url = WidgetContainer.snapshotURL(bundle: bundle),
              let data = try? Data(contentsOf: url)
        else {
            return nil
        }
        return try? JSONDecoder().decode(WidgetSnapshot.self, from: data)
    }

    public static func state(
        from snapshot: WidgetSnapshot?,
        emptyMessage: String,
        seasonEpisodeLabel: (Int64, Int64) -> String,
        destination: (WidgetSnapshotEntry) -> URL?,
        bundle: Bundle = .main
    ) -> UpNextWidgetState {
        guard let snapshot, !snapshot.entries.isEmpty else {
            return .empty(message: emptyMessage)
        }

        let items = snapshot.entries.map { entry in
            UpNextItem(
                id: entry.tmdbId,
                showName: entry.showName,
                episodeName: entry.episodeName,
                seasonEpisodeLabel: seasonEpisodeLabel(entry.seasonNumber, entry.episodeNumber),
                poster: poster(for: entry, bundle: bundle),
                destination: destination(entry)
            )
        }
        return .content(items: items, lastUpdated: nil)
    }

    private static func poster(for entry: WidgetSnapshotEntry, bundle: Bundle) -> UIImage? {
        guard let fileName = entry.posterFileName,
              let folder = WidgetContainer.postersURL(bundle: bundle)
        else {
            return nil
        }
        return UIImage(contentsOfFile: folder.appendingPathComponent(fileName).path)
    }
}
