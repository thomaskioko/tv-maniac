package com.thomaskioko.tvmaniac.iosframework.widget

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.widget.PosterDownloader
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToFile
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@OptIn(ExperimentalForeignApi::class)
public class IosPosterDownloader(
    private val logger: Logger,
) : PosterDownloader {

    override suspend fun download(url: String, directoryPath: String, fileName: String): Boolean {
        val destination = "$directoryPath/$fileName"
        if (NSFileManager.defaultManager.fileExistsAtPath(destination)) return true

        val remoteUrl = NSURL.URLWithString(url) ?: return false
        val data = NSData.dataWithContentsOfURL(remoteUrl) ?: return false
        val image = UIImage.imageWithData(data) ?: return false
        val jpeg = UIImageJPEGRepresentation(image, JPEG_QUALITY) ?: return false

        NSFileManager.defaultManager.createDirectoryAtPath(
            path = directoryPath,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )

        val written = jpeg.writeToFile(destination, atomically = true)
        if (!written) logger.error(TAG, "Could not write poster to $destination")
        return written
    }

    override fun deleteExcept(directoryPath: String, fileNames: Set<String>) {
        val manager = NSFileManager.defaultManager
        val existing = manager.contentsOfDirectoryAtPath(directoryPath, error = null) ?: return

        existing.filterIsInstance<String>()
            .filterNot { fileNames.contains(it) }
            .forEach { manager.removeItemAtPath("$directoryPath/$it", error = null) }
    }

    private companion object {
        private const val TAG = "IosPosterDownloader"
        private const val JPEG_QUALITY = 0.8
    }
}
