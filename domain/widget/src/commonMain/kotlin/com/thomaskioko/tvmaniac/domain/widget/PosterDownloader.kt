package com.thomaskioko.tvmaniac.domain.widget

public interface PosterDownloader {

    public suspend fun download(url: String, directoryPath: String, fileName: String): Boolean

    public fun deleteExcept(directoryPath: String, fileNames: Set<String>)
}
