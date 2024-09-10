import SwiftUI
import SwiftUIComponents

public struct ShowInfoView: View {
    private let isFollowed: Bool
    private let openTrailersInYoutube: Bool
    private let genreList: [SwiftGenres]
    private let seasonList: [SwiftSeason]
    private let providerList: [SwiftProviders]
    private let trailerList: [SwiftTrailer]
    private let castsList: [SwiftCast]
    private let recommendedShowList: [SwiftShow]
    private let similarShows: [SwiftShow]
    private let onWatchTrailer: () -> Void
    private let onAddToLibrary: () -> Void
    private let onSeasonClicked: (Int, SwiftSeason) -> Void
    private let onShowClicked: () -> Void
    @Binding var titleRect: CGRect

    public init(
        isFollowed: Bool,
        openTrailersInYoutube: Bool,
        genreList: [SwiftGenres],
        seasonList: [SwiftSeason],
        providerList: [SwiftProviders],
        trailerList: [SwiftTrailer],
        castsList: [SwiftCast],
        recommendedShowList: [SwiftShow],
        similarShows: [SwiftShow],
        onWatchTrailer: @escaping () -> Void,
        onAddToLibrary: @escaping () -> Void,
        onSeasonClicked: @escaping (Int, SwiftSeason) -> Void,
        onShowClicked: @escaping () -> Void,
        titleRect: Binding<CGRect>
    ) {
        self.isFollowed = isFollowed
        self.openTrailersInYoutube = openTrailersInYoutube
        self.genreList = genreList
        self.seasonList = seasonList
        self.providerList = providerList
        self.trailerList = trailerList
        self.castsList = castsList
        self.recommendedShowList = recommendedShowList
        self.similarShows = similarShows
        self.onWatchTrailer = onWatchTrailer
        self.onAddToLibrary = onAddToLibrary
        self.onSeasonClicked = onSeasonClicked
        self.onShowClicked = onShowClicked
        self._titleRect = titleRect
    }

    public var body: some View {
        VStack {
            Spacer(minLength: nil)
                .background(GeometryGetter(rect: self.$titleRect))

            if !genreList.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(alignment: .center) {
                        ForEach(genreList, id: \.name) { item in
                            ChipView(label: item.name)
                        }
                    }
                    .padding(.horizontal)
                }
            }

            HStack(alignment: .center, spacing: 8) {
                OutlinedButton(
                    text: "Watch Trailer",
                    systemImageName: "film.fill",
                    action: onWatchTrailer
                )

                let followText = isFollowed ? "Unfollow Show" : "Follow Show"
                let buttonSystemImage = isFollowed ? "checkmark.square.fill" : "plus.square.fill.on.square.fill"

                OutlinedButton(
                    text: followText,
                    systemImageName: buttonSystemImage,
                    action: onAddToLibrary
                )
            }
            .padding(.top, 10)

            SeasonChipViewList(
                items: seasonList,
                onClick: { index in
                    onSeasonClicked(index, seasonList[index])
                }
            )

            ProviderListView(items: providerList)

            TrailerListView(
                trailers: trailerList,
                openInYouTube: openTrailersInYoutube
            )

            CastListView(casts: castsList)

            HorizontalItemListView(
                title: "Recommendations",
                items: recommendedShowList,
                onClick: { _ in onShowClicked() }
            )

            HorizontalItemListView(
                title: "Similar Shows",
                items: similarShows,
                onClick: { _ in onShowClicked() }
            )
        }
    }
}

#Preview {
    VStack {
        Spacer(minLength: 520)

        ShowInfoView(
            isFollowed: true,
            openTrailersInYoutube: false,
            genreList: [
                .init(name: "Sci-Fi"),
                .init(name: "Horror"),
                .init(name: "Action"),
            ],
            seasonList: [
                .init(tvShowId: 23, seasonId: 23, seasonNumber: 1, name: "Season 1"),
                .init(tvShowId: 123, seasonId: 123, seasonNumber: 2, name: "Season 2"),
            ],
            providerList: [
                .init(
                    providerId: 123,
                    logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/4KAy34EHvRM25Ih8wb82AuGU7zJ.png"
                ),
                .init(
                    providerId: 1233,
                    logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/alqLicR1ZMHMaZGP3xRQxn9sq7p.png"
                ),
                .init(
                    providerId: 23,
                    logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/wwemzKWzjKYJFfCeiB57q3r4Bcm.png"
                ),
            ],
            trailerList: [
                .init(
                    showId: 123,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: "https://i.ytimg.com/vi/XZ8daibM3AE/hqdefault.jpg"
                ),
                .init(
                    showId: 1234,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: "https://i.ytimg.com/vi/XZ8daibM3AE/hqdefault.jpg"
                ),
            ],
            castsList: [
                .init(
                    castId: 123,
                    name: "Rosario Dawson",
                    characterName: "Claire Temple",
                    profileUrl: "https://image.tmdb.org/t/p/w780/1mm7JGHIUX3GRRGXEV9QCzsI0ao.jpg"
                ),
                .init(
                    castId: 1234,
                    name: "Hailee Steinfeld",
                    characterName: "Hailee Steinfeld",
                    profileUrl: "https://image.tmdb.org/t/p/w780/6aBclBl8GMcxbxr6XcwSGg3IBea.jpg"
                ),
                .init(
                    castId: 1235,
                    name: "内田夕夜",
                    characterName: "Yuuya Uchida",
                    profileUrl: "https://image.tmdb.org/t/p/w780/4xLLQGEDWtmLWUapo0UnfvCdsXp.jpg"
                ),
            ],
            recommendedShowList: [
                .init(
                    tmdbId: 1234,
                    title: "Arcane",
                    posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 12346,
                    title: "Kaos",
                    posterUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            similarShows: [
                .init(
                    tmdbId: 1234,
                    title: "Arcane",
                    posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 12346,
                    title: "Kaos",
                    posterUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            onWatchTrailer: {},
            onAddToLibrary: {},
            onSeasonClicked: { _, _ in },
            onShowClicked: {},
            titleRect: .constant(CGRect())
        )
    }
    .background(Color.background)
}
