import SwiftUI

public struct HeaderView: View {
    private let title: String
    private let overview: String
    private let backdropImageUrl: String?
    private let status: String?
    private let year: String
    private let language: String?
    private let rating: Double
    private let progress: CGFloat
    private let headerHeight: CGFloat

    public init(
        title: String,
        overview: String,
        backdropImageUrl: String?,
        status: String?,
        year: String,
        language: String?,
        rating: Double,
        progress: CGFloat,
        headerHeight: CGFloat = 460
    ) {
        self.title = title
        self.overview = overview
        self.backdropImageUrl = backdropImageUrl
        self.status = status
        self.year = year
        self.language = language
        self.rating = rating
        self.progress = progress
        self.headerHeight = headerHeight
    }

    public var body: some View {
        ZStack(alignment: .bottom) {
            HeaderCoverArtWorkView(
                backdropImageUrl: backdropImageUrl,
                posterHeight: headerHeight
            )
            .foregroundStyle(.ultraThinMaterial)
            .overlay(
                LinearGradient(
                    gradient: Gradient(colors: [
                        .clear,
                        .clear,
                        .clear,
                        Color.background.opacity(0),
                        Color.background.opacity(0.8),
                        Color.background.opacity(0.97),
                        Color.background,
                        Color.background,
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
            .frame(height: headerHeight)

            VStack {
                Spacer()
                ShowHeaderInfoView(
                    title: title,
                    overview: overview,
                    status: status,
                    year: year,
                    language: language,
                    rating: rating
                )
                .opacity(1 - progress)
            }
            .frame(height: headerHeight)
        }
        .frame(height: headerHeight)
        .clipped()
    }
}

struct ShowHeaderInfoView: View {
    let title: String
    let overview: String
    let status: String?
    let year: String
    let language: String?
    let rating: Double

    var body: some View {
        VStack(spacing: 0) {
            Text(title)
                .titleFont(size: 30)
                .foregroundColor(.textColor)
                .lineLimit(1)
                .padding(.top, 12)
                .padding([.leading, .trailing], 16)

            OverviewBoxView(overview: overview)

            showDetailMetadata
        }
        .padding([.trailing, .leading, .bottom], 16)
    }

    private var showDetailMetadata: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(alignment: .center) {
                if let status = status, !status.isEmpty {
                    BorderTextView(
                        text: status,
                        backgroundColor: Color.accent.opacity(0.12),
                        borderColor: Color.accent.opacity(0.12),
                        weight: .bold
                    )
                }

                BorderTextView(text: year)

                if let language = language {
                    BorderTextView(text: language)
                }

                BorderTextView(text: String(format: "%.1f", rating))
            }
        }
    }
}

#Preview {
    VStack {
        HeaderView(
            title: "Arcane",
            overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            backdropImageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            status: "Continuing",
            year: "2024",
            language: "EN",
            rating: 4.8,
            progress: 0
        )

        Spacer()
    }
    .edgesIgnoringSafeArea(.top)
}
