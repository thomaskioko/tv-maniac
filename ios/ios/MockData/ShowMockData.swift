//
//  ShowMockData.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 15.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import TvManiac

var mockShow = ShowUiModel(
	id: 1,
	title: "Money Heist",
	overview: "A criminal mastermind who goes by The Professor has a plan to pull off the biggest heist in recorded history -- to print billions of euros in the Royal Mint of Spain. To help him carry out the ambitious plan, he recruits eight people with certain abilities and who have nothing to lose",
	language: "EN",
	posterImageUrl: "",
	backdropImageUrl: "",
	year: "2012",
	status: "Ended",
	votes: 21,
	averageVotes: 4.8,
	isInWatchlist: false,
	genreIds: [12,15]
)

var genreList = [
	GenreUIModel(id: 12, name: "Drama"),
	GenreUIModel(id: 15, name: "Action"),
	GenreUIModel(id: 16, name: "Sci-Fi"),
	GenreUIModel(id: 64, name: "Animation")
]
