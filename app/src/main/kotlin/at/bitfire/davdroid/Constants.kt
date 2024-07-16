/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */
package at.bitfire.davdroid

import android.net.Uri
import androidx.core.net.toUri

/**
 * Brand-specific constants like (non-theme) colors, homepage URLs etc.
 */
object Constants {

    const val DAVDROID_GREEN_RGBA = 0xFF8bc34a.toInt()

    val HOMEPAGE_URL = "https://www.davx5.com".toUri()
    val COZYLA_STORE_URL = "https://www.cozyla.com/".toUri()
    val TUTORIAL_VIDEOS  = "https://www.cozyla.com/calendartutorial".toUri()
    val COZYLA_FAQ_URL = "https://support.cozyla.com/hc/en-us/categories/26643447703579-Cozyla-Calendar".toUri()
    val COZYLA_SUPPORT_URL = "https://support.cozyla.com/hc/en-us/categories/26643447703579-Cozyla-Calendar".toUri()
    const val HOMEPAGE_PATH_FAQ = "faq"
    const val HOMEPAGE_PATH_FAQ_SYNC_NOT_RUN = "synchronization-is-not-run-as-expected"
    const val HOMEPAGE_PATH_FAQ_LOCATION_PERMISSION = "wifi-ssid-restriction-location-permission"
    const val HOMEPAGE_PATH_OPEN_SOURCE = "donate"
    const val HOMEPAGE_PATH_PRIVACY = "privacy"
    const val HOMEPAGE_PATH_TESTED_SERVICES = "tested-with"

    val MANUAL_URL = "https://manual.davx5.com".toUri()

    val OPEN_SOURECE = "https://github.com/zqgzyf/cozyla-davx".toUri()
    const val MANUAL_PATH_ACCOUNTS_COLLECTIONS = "accounts_collections.html"
    const val MANUAL_FRAGMENT_SERVICE_DISCOVERY = "how-does-service-discovery-work"
    const val MANUAL_PATH_SETTINGS = "settings.html"
    const val MANUAL_FRAGMENT_APP_SETTINGS = "app-wide-settings"
    const val MANUAL_FRAGMENT_ACCOUNT_SETTINGS = "account-settings"
    const val MANUAL_PATH_WEBDAV_MOUNTS = "webdav_mounts.html"

    val COMMUNITY_URL = "https://github.com/bitfireAT/davx5-ose/discussions".toUri()

    val FEDIVERSE_HANDLE = "@davx5app@fosstodon.org"
    val FEDIVERSE_URL = "https://fosstodon.org/@davx5app".toUri()

    /**
     * Appends query parameters for anonymized usage statistics (app ID, version).
     * Can be used by the called Website to get an idea of which versions etc. are currently used.
     *
     * @param context   optional info about from where the URL was opened (like a specific Activity)
     */
    fun Uri.Builder.withStatParams(context: String? = null): Uri.Builder {
        appendQueryParameter("pk_campaign", BuildConfig.APPLICATION_ID)
        appendQueryParameter("app-version", BuildConfig.VERSION_NAME)

        if (context != null)
            appendQueryParameter("pk_kwd", context)

        return this
    }

}