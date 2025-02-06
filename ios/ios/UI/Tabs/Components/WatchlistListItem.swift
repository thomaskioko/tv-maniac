import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct WatchlistListItem: View {
  let item: TvManiac.WatchlistItem
  let namespace: Namespace.ID

  var body: some View {
    HStack(spacing: 0) {
      PosterItemView(
        title: item.title,
        posterUrl: item.posterImageUrl,
        posterWidth: WatchlistListItemConstants.posterWidth,
        posterHeight: WatchlistListItemConstants.height
      )

      watchlistItemDetails(item: item)
    }
    .frame(maxWidth: .infinity)
    .frame(height: WatchlistListItemConstants.height)
    .background(Color.content_background)
    .cornerRadius(6)
    .matchedGeometryEffect(id: item.tmdbId, in: namespace)
  }

  @ViewBuilder
  private func watchlistItemDetails(item: TvManiac.WatchlistItem) -> some View {
    ZStack(alignment: .bottom) {
      VStack(alignment: .leading, spacing: 4) {
        Text(item.title)
          .font(.avenirNext(size: 18))
          .fontWeight(.semibold)
          .foregroundColor(.textColor)
          .lineLimit(1)

        HStack(spacing: 4) {
          if item.seasonCount > 0 {
            Text("^[\(item.seasonCount) Season](inflect: true)")
              .font(.caption)
              .foregroundColor(.gray)
          }

          if item.episodeCount > 0 {
            Text("•")
              .font(.avenirNext(size: 8))
              .foregroundColor(.secondary)

            Text("^[\(item.episodeCount) Episode](inflect: true)")
              .font(.caption)
              .foregroundColor(.gray)
          }
        }

        HStack(spacing: 4) {
          if let status = item.status {
            BorderTextView(
              text: status,
              colorOpacity: 0.12,
              borderOpacity: 0.12,
              weight: .bold
            )

            Text("•")
              .font(.avenirNext(size: 8))
              .foregroundColor(.secondary)
          }

          if let year = item.year {
            Text("\(year)")
              .font(.caption)
                        .foregroundColor(.gray)
          }
        }
        .padding(.top, 4)

        Spacer()
      }
      .frame(maxWidth: .infinity, alignment: .leading)
      .padding(.vertical)
      .padding(.horizontal, 8)

      ProgressView(value: 0, total: 1)
        .progressViewStyle(RoundedRectProgressViewStyle())
        .offset(y: 2)
    }
  }
}

// Extract item details into separate view

public enum WatchlistListItemConstants {
  static let height: CGFloat = 140
  static let posterWidth: CGFloat = 100
}
