//
//  ShowMockData.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import TvManiac


var mockShow = TvShow(
	traktId: 1,
	tmdbId: 45,
	title: "Money Heist",
	overview: "A criminal mastermind who goes by The Professor has a plan to pull off the biggest heist in recorded history -- to print billions of euros in the Royal Mint of Spain. To help him carry out the ambitious plan, he recruits eight people with certain abilities and who have nothing to lose",
	language: "EN",
	posterImageUrl: "",
	backdropImageUrl: "https://image.tmdb.org/t/p/original/tayfazRzkhpyKAhKS6PALXKJUj1.jpg",
	year: "2012",
	status: "Ended",
	votes: 21,
	numberOfSeasons: 2,
	numberOfEpisodes: 45,
	rating: 4.5,
	genres: ["Horror", "Action"]
)

var genreList = ["Drama","Action", "Sci-Fi", "Animation"]

var episodeList = [
	LastAirEpisode(
		id: 123,
		name: "S1.E04 • The Chaod Leass Traveled",
		overview: "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
		"an alternate version of Loki is brought to the mysterious Time Variance " +
		"Authority, a bureaucratic organization that exists outside of time and " +
		"space and monitors the timeline. They give Loki a choice: face being " +
		"erased from existence due to being a “time variant”or help fix " +
		"the timeline and stop a greater threat.",
		airDate: "Thu, Jan 20, 2022",
		episodeNumber: 1,
		seasonNumber: 1,
		posterPath: "",
		voteAverage: 4.5,
		voteCount: 1234,
		title: "Latest Release"
	),
	LastAirEpisode(
		id: 123,
		name: "S1.E04 • The Chaod Leass Traveled",
		overview: "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
		"an alternate version of Loki is brought to the mysterious Time Variance " +
		"Authority, a bureaucratic organization that exists outside of time and " +
		"space and monitors the timeline. They give Loki a choice: face being " +
		"erased from existence due to being a “time variant”or help fix " +
		"the timeline and stop a greater threat.",
		airDate: "Thu, Jan 20, 2022",
		episodeNumber: 1,
		seasonNumber: 1,
		posterPath: "",
		voteAverage: 4.5,
		voteCount: 1234,
		title: "Coming Soon"
	)
]

var seasonList = [
	SeasonUiModel(seasonId: 123, tvShowId: 123, name: "Season 1", overview: "", seasonNumber: 1, episodeCount: 12),
	SeasonUiModel(seasonId: 13, tvShowId: 13, name: "Season 2", overview: "", seasonNumber: 1, episodeCount: 12)
]

var viewState = ShowDetailUiViewState(
	isLoading: false,
	errorMessage: "",
	tvShow: mockShow,
	similarShowList: [mockShow, mockShow, mockShow, mockShow],
	seasonList: seasonList
)


//Get rid of this class once we implement show detail stateMachine
class ShowDetailUiViewState {
	var isLoading: Bool
	var errorMessage: String?
	var tvShow: TvShow
	var similarShowList: [TvShow]
	var seasonList: [SeasonUiModel]
	
	init(isLoading: Bool, errorMessage: String? = nil, tvShow: TvShow, similarShowList: [TvShow], seasonList: [SeasonUiModel]) {
		self.isLoading = isLoading
		self.errorMessage = errorMessage
		self.tvShow = tvShow
		self.similarShowList = similarShowList
		self.seasonList = seasonList
	}
}
