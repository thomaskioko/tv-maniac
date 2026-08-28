package com.thomaskioko.tvmaniac.domain.widget

class FakePosterDownloader : PosterDownloader {
    private var succeeds: Boolean = true
    private val requestedUrls = mutableListOf<String>()
    private var keptFileNames: Set<String>? = null

    fun setSucceeds(succeeds: Boolean) {
        this.succeeds = succeeds
    }

    fun getRequestedUrls(): List<String> = requestedUrls.toList()

    fun getKeptFileNames(): Set<String>? = keptFileNames

    override suspend fun download(url: String, directoryPath: String, fileName: String): Boolean {
        requestedUrls += url
        return succeeds
    }

    override fun deleteExcept(directoryPath: String, fileNames: Set<String>) {
        keptFileNames = fileNames
    }
}
