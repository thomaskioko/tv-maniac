import SwiftUI

public struct ShowHeaderInfoView: View {
    private let title: String
    private let overview: String
    private let status: String?
    private let year: String
    private let language: String?
    private let rating: Double

    public init(
        title: String,
        overview: String,
        status: String?,
        year: String,
        language: String?,
        rating: Double
    ) {
        self.title = title
        self.overview = overview
        self.status = status
        self.year = year
        self.language = language
        self.rating = rating
    }

    public var body: some View {
        VStack(spacing: 0) {
            Text(title)
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.textColor)
                .lineLimit(1)
                .padding(.horizontal)
                .frame(maxWidth: .infinity, alignment: .center)

            showDetailMetadata
                .padding(.horizontal, 16)
                .padding(.vertical, 8)

            OverviewBoxView(overview: overview)
                .padding(.horizontal, 16)
        }
    }

    private var showDetailMetadata: some View {
        HStack(alignment: .center) {
            if let status, !status.isEmpty {
                BorderTextView(
                    text: status,
                    colorOpacity: 0.12,
                    borderOpacity: 0.12,
                    weight: .bold
                )

                Text("•")
                    .font(.avenirNext(size: 8))
                    .foregroundColor(.accent)
            }

            Text(year)
                .font(.avenirNext(size: 14))
                .fontWeight(.semibold)

            if let language {
                Text("•")
                    .font(.avenirNext(size: 8))
                    .foregroundColor(.accent)

                Text(language)
                    .font(.avenirNext(size: 14))
                    .fontWeight(.semibold)
            }

            Text("•")
                .font(.avenirNext(size: 8))
                .foregroundColor(.accent)

            Image(systemName: "star.fill")
                .resizable()
                .frame(width: 14, height: 14)
                .foregroundColor(.accent)

            Text(String(format: "%.1f", rating))
                .font(.avenirNext(size: 14))
                .fontWeight(.semibold)

            Text("•")
                .font(.avenirNext(size: 8))
                .foregroundColor(.accent)
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }
}

#Preview {
    VStack {
        ShowHeaderInfoView(
            title: "Arcane",
            overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            status: "Ended",
            year: "2024",
            language: "EN",
            rating: 4.8
        )
    }
    .background(Color.background)
}
