//
//  ShowMockData.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import TvManiac


var mockTvShow = TvShow(
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
	genres: ["Horror", "Action"],
	isFollowed: false
)

var mockShow = Show(
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
		genres: ["Horror", "Action"],
		isFollowed: false
)

var genreList = ["Drama","Action", "Sci-Fi", "Animation"]


var seasonList = [
	Season(seasonId: 123, tvShowId: 123, name: "Season 1"),
	Season(seasonId: 13, tvShowId: 13, name: "Season 2")
]

var detailState = ShowDetailsStateShowDetailsLoaded(
		showState: ShowStateShowLoaded(show: mockShow),
		similarShowsState: SimilarShowsStateSimilarShowsError(errorMessage: ""),
		seasonState: SeasonStateSeasonsError(errorMessage: ""),
		trailerState: TrailersStateTrailersError(errorMessage: ""),
		followShowState: FollowShowsStateIdle()
)

//Get rid of this class once we implement show detail stateMachine
class ShowDetailUiViewState {
	var isLoading: Bool
	var errorMessage: String?
	var tvShow: TvShow
	var similarShowList: [TvShow]
	var seasonList: [Season]
	
	init(isLoading: Bool, errorMessage: String? = nil, tvShow: TvShow, similarShowList: [TvShow], seasonList: [Season]) {
		self.isLoading = isLoading
		self.errorMessage = errorMessage
		self.tvShow = tvShow
		self.similarShowList = similarShowList
		self.seasonList = seasonList
	}
}
