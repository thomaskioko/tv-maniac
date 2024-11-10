import SwiftUI
import SwiftUIComponents

public struct SearchItemView: View {
  private let title: String
  private let overview: String?
  private let imageUrl: String?
  private let status: String?
  private let year: String?
  private let voteAverage: Double?
  private let imageWidth: CGFloat
  private let imageHeight: CGFloat
  private let shadowRadius: CGFloat
  private let cornerRadius: CGFloat
  private let imageRadius: CGFloat

  public init(
    title: String,
    overview: String?,
    imageUrl: String?,
    status: String?,
    year: String?,
    voteAverage: Double?,
    imageWidth: CGFloat = 120,
    imageHeight: CGFloat = 140,
    shadowRadius: CGFloat = 2.5,
    cornerRadius: CGFloat = 4,
    imageRadius: CGFloat = 4
  ) {
    self.title = title
    self.overview = overview
    self.imageUrl = imageUrl
    self.status = status
    self.year = year
    self.voteAverage = voteAverage
    self.imageWidth = imageWidth
    self.imageHeight = imageHeight
    self.shadowRadius = shadowRadius
    self.cornerRadius = cornerRadius
    self.imageRadius = imageRadius
  }

  public var body: some View {
    HStack {
      PosterItemView(
        title: nil,
        posterUrl: imageUrl,
        posterWidth: imageWidth,
        posterHeight: imageHeight,
        posterRadius: imageRadius
      )
      .padding(.trailing, 4)

      VStack(alignment: .leading, spacing: 4) {
        Text(title)
          .font(.headline)
          .lineLimit(1)

        HStack(spacing: 4) {
          if let voteAverage = voteAverage {
            Image(systemName: "star.fill")
              .foregroundColor(.accent)

            Text(String(format: "%.1f", voteAverage))
              .font(.avenirNext(size: 14))
          }

          if let status = status, !status.isEmpty {
            Text("•")
              .font(.avenirNext(size: 8))
              .foregroundColor(.accent)

            BorderTextView(
              text: status,
              colorOpacity: 0.12,
              borderOpacity: 0.12,
              weight: .bold
            )
          }

          if let year = year {
            Text("•")
              .font(.avenirNext(size: 8))
              .foregroundColor(.accent)

            Text(year)
              .font(.avenirNext(size: 14))
          }
        }
        .font(.subheadline)
        .foregroundColor(.gray)

        Text(overview ?? "")
          .font(.avenirNext(size: 14))
          .foregroundColor(.textColor)
          .lineLimit(3)
          .padding(.trailing, 8)
      }
      Spacer()
    }
    .frame(maxWidth: .infinity)
    .frame(height: imageHeight)
    .background(Color.content_background)
    .cornerRadius(cornerRadius)
  }
}

#Preview {
  VStack {
    SearchItemView(
      title: "Arcane",
      overview: "In 1997, a haunted scientist brushes his family aside for an all-consuming project. In 2022, a renegade fighter battles a powerful robot for vital data.",
      imageUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/8rjILRAlcvI9y7vJuH9yNjKYhta.jpg",
      status: "Ended",
      year: "2024",
      voteAverage: 5.4
    )

    SearchItemView(
      title: "Arcane",
      overview: "",
      imageUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/8rjILRAlcvI9y7vJuH9yNjKYhta.jpg",
      status: "Ended",
      year: "2024",
      voteAverage: 5.4
    )
  }
}
