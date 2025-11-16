package com.thomaskioko.tvmaniac.buildconfig.implementation

import com.thomaskioko.tvmaniac.buildconfig.api.BuildConfigRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import me.tatarka.inject.annotations.Inject
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@OptIn(ExperimentalForeignApi::class)
public class IosBuildConfigRepository : BuildConfigRepository {

    override suspend fun getTmdbApiKey(): String? = getKeychainValue(KEY_TMDB_API_KEY)

    override suspend fun getTraktClientId(): String? = getKeychainValue(KEY_TRAKT_CLIENT_ID)

    override suspend fun getTraktClientSecret(): String? = getKeychainValue(KEY_TRAKT_CLIENT_SECRET)

    override suspend fun setTmdbApiKey(key: String) {
        setKeychainValue(KEY_TMDB_API_KEY, key)
    }

    override suspend fun setTraktClientId(id: String) {
        setKeychainValue(KEY_TRAKT_CLIENT_ID, id)
    }

    override suspend fun setTraktClientSecret(secret: String) {
        setKeychainValue(KEY_TRAKT_CLIENT_SECRET, secret)
    }

    override suspend fun clearAll() {
        deleteKeychainValue(KEY_TMDB_API_KEY)
        deleteKeychainValue(KEY_TRAKT_CLIENT_ID)
        deleteKeychainValue(KEY_TRAKT_CLIENT_SECRET)
    }

    override suspend fun isConfigured(): Boolean {
        return getKeychainValue(KEY_TMDB_API_KEY) != null &&
            getKeychainValue(KEY_TRAKT_CLIENT_ID) != null &&
            getKeychainValue(KEY_TRAKT_CLIENT_SECRET) != null
    }

    private fun getKeychainValue(key: String): String? = memScoped {
        val query = createQuery(key)
        CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)

        val result = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, result.ptr)

        if (status == errSecSuccess) {
            val data = CFBridgingRelease(result.value) as? platform.Foundation.NSData
            data?.let {
                NSString.create(it, NSUTF8StringEncoding) as? String
            }
        } else {
            null
        }
    }

    private fun setKeychainValue(key: String, value: String) {
        // First, try to delete existing value
        deleteKeychainValue(key)

        // Then add new value
        val valueData = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            ?: return

        val query = createQuery(key)
        CFDictionarySetValue(query, kSecValueData, CFBridgingRetain(valueData))
        CFDictionarySetValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)

        SecItemAdd(query, null)
    }

    private fun deleteKeychainValue(key: String) {
        val query = createQuery(key)
        SecItemDelete(query)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createQuery(key: String): platform.CoreFoundation.CFMutableDictionaryRef {
        val dict = CFDictionaryCreateMutable(
            null,
            3,
            null,
            null,
        ) as platform.CoreFoundation.CFMutableDictionaryRef

        CFDictionarySetValue(dict, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(dict, kSecAttrService, CFBridgingRetain(SERVICE_NAME as NSString))
        CFDictionarySetValue(dict, kSecAttrAccount, CFBridgingRetain(key as NSString))

        return dict
    }

    private companion object {
        private const val SERVICE_NAME = "com.thomaskioko.tvmaniac.config"
        private const val KEY_TMDB_API_KEY = "tmdb_api_key"
        private const val KEY_TRAKT_CLIENT_ID = "trakt_client_id"
        private const val KEY_TRAKT_CLIENT_SECRET = "trakt_client_secret"
    }
}
