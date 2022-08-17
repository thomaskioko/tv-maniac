package com.thomaskioko.tvmaniac.datasource.network.api

import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.datasource.network.TvShowsServiceMockEngine
import com.thomaskioko.tvmaniac.datasource.network.mockresponse.getPopularTvShows
import com.thomaskioko.tvmaniac.datasource.network.mockresponse.getTvSeasonDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbServiceImpl
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
internal class TvShowServiceTest : TvShowsServiceMockEngine() {

    companion object {
        const val GET_TV_SHOW_DETAILS = "https://api.themoviedb.org/3/tv/1"
        const val GET_POPULAR_TV_SHOWS = "https://api.themoviedb.org/3/tv/popular?page=1&sort_by=popularity.desc"
    }

    @Test
    fun whenGetTvSeasonDetails_correctUrlIsCalled(): Unit = runBlockingTest {

        val apiClient = givenAMockTvShowsService(
            GET_TV_SHOW_DETAILS,
            httpStatusCode = 200,
            responseBody = getTvSeasonDetailsResponse() // TODO Read this from json file
        )

        apiClient.getTvShowDetails(1)

        verifyGetRequest()
        verifyRequestContainsHeader("Accept", "application/json")
    }

    @Test
    fun whenPopularShows_correctUrlIsCalled(): Unit = runBlockingTest {

        val apiClient = givenAMockTvShowsService(
            endPoint = GET_POPULAR_TV_SHOWS,
            httpStatusCode = 200,
            responseBody = getPopularTvShows() // TODO Read this from json file
        )

        apiClient.getPopularShows(1)

        verifyGetRequest()
        verifyRequestContainsHeader("Accept", "application/json")
    }

    private fun givenAMockTvShowsService(
        endPoint: String,
        responseBody: String = "",
        httpStatusCode: Int = 200
    ): TmdbServiceImpl {
        enqueueMockResponse(endPoint, responseBody, httpStatusCode)

        return TmdbServiceImpl(httpClient())
    }
}
