package com.thomaskioko.tvmaniac.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile
import platform.darwin.NSObject
import platform.darwin.NSObjectMeta
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class BundleResourceReader(
    private val bundle: NSBundle = NSBundle.bundleForClass(BundleMarker),
) : ResourceReader {

    override fun readResource(name: String): String {
        // TODO: Catch iOS-only exceptions and map them to common ones.
        val (filename, type) = when (val lastPeriodIndex = name.lastIndexOf('.')) {
            0 -> null to name.drop(1)
            in 1..Int.MAX_VALUE -> {
                name.take(lastPeriodIndex) to name.drop(lastPeriodIndex + 1)
            }
            else -> name to null
        }

        var path = bundle.pathForResource(filename, type)

        // If not found in the framework bundle, try to find it in the main bundle
        if (path == null) {
            path = NSBundle.mainBundle.pathForResource(filename, type)
        }

        // If still not found, throw an error
        if (path == null) {
            error(
                "Couldn't get path of $name (parsed as: ${listOfNotNull(filename, type).joinToString(".")})",
            )
        }

        return memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()

            NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, error = errorPtr.ptr)
                ?: run {
                    // TODO: Check the NSError and throw common exception.
                    error(
                        "Couldn't load resource: $name. Error: ${errorPtr.value?.localizedDescription} - ${errorPtr.value}",
                    )
                }
        }
    }

    private class BundleMarker : NSObject() {
        companion object : NSObjectMeta()
    }
}

data class BundleProvider(val bundle: NSBundle)
