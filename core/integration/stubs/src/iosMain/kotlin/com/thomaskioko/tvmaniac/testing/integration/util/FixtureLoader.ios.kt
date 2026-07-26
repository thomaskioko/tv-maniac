package com.thomaskioko.tvmaniac.testing.integration.util

import platform.Foundation.NSProcessInfo
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

/**
 * Kotlin/Native has no resource mechanism and the framework is static, so the caller names a
 * filesystem path instead. Simulator processes can read the host filesystem, so nothing is copied
 * into the app.
 */
public const val FIXTURE_DIR_ENV: String = "TVMANIAC_FIXTURE_DIR"

internal actual fun readFixture(resourcePath: String): String {
    val directory = NSProcessInfo.processInfo.environment[FIXTURE_DIR_ENV] as String?
    checkNotNull(directory) {
        "$FIXTURE_DIR_ENV is not set — cannot load fixture: $resourcePath"
    }
    val file = "${directory.trimEnd('/')}/$resourcePath"
    val contents = NSString.stringWithContentsOfFile(file, NSUTF8StringEncoding, null)
    return checkNotNull(contents) { "Fixture not found on disk: $file" }
}
