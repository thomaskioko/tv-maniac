import SwiftUI

public struct HeaderView: View {
    @Theme private var theme

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
                imageUrl: backdropImageUrl,
                posterHeight: headerHeight
            )
            .foregroundStyle(.ultraThinMaterial)
            .overlay(
                LinearGradient(
                    gradient: Gradient(colors: [
                        .clear,
                        .clear,
                        theme.colors.background.opacity(0.1),
                        theme.colors.background.opacity(0.3),
                        theme.colors.background.opacity(0.6),
                        theme.colors.background.opacity(0.9),
                        theme.colors.background,
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
                .padding(.bottom, theme.spacing.xxxSmall)
            }
            .frame(height: headerHeight)
        }
        .frame(height: headerHeight)
        .clipped()
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
