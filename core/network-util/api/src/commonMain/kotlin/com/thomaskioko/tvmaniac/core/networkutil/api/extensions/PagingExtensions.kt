package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse

public suspend fun <T> fetchPages(
    limit: Int,
    firstPage: Int = 1,
    request: suspend (page: Int, limit: Int) -> ApiResponse<List<T>>,
): ApiResponse<List<T>> {
    val items = mutableListOf<T>()
    var page = firstPage
    while (true) {
        val response = request(page, limit)
        if (response !is ApiResponse.Success) return response
        items += response.body
        if (response.body.size < limit) return ApiResponse.Success(items)
        page++
    }
}
