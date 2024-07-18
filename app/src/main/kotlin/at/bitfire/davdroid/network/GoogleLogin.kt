/***************************************************************************************************
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 **************************************************************************************************/

package at.bitfire.davdroid.network

import android.net.Uri
import at.bitfire.davdroid.BuildConfig
import at.bitfire.davdroid.db.Credentials
import at.bitfire.davdroid.log.Logger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse
import java.net.URI

class GoogleLogin(
    val authService: AuthorizationService
) {

    companion object {

        // davx5integration@gmail.com (for davx5-ose)
        private const val CLIENT_ID = "561076319877-2bjf5tmfamm35vr7vh1mjf7r14nil37g.apps.googleusercontent.com"

        private val SCOPES = arrayOf(
            "https://www.googleapis.com/auth/calendar",     // CalDAV
//            "https://www.googleapis.com/auth/carddav"       // CardDAV
        )

        /**
         * Gets the Google CalDAV/CardDAV base URI. See https://developers.google.com/calendar/caldav/v2/guide;
         * _calid_ of the primary calendar is the account name.
         *
         * This URL allows CardDAV (over well-known URLs) and CalDAV detection including calendar-homesets and secondary
         * calendars.
         */
        fun googleBaseUri(googleAccount: String): URI =
            URI("https", "apidata.googleusercontent.com", "/caldav/v2/$googleAccount/user", null)

        private val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.google.com/o/oauth2/auth"),
            Uri.parse("https://oauth2.googleapis.com/token")
        )

    }

    fun signIn(email: String, customClientId: String?, locale: String?): AuthorizationRequest {
        val builder = AuthorizationRequest.Builder(
            GoogleLogin.serviceConfig,
            customClientId ?: CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(BuildConfig.APPLICATION_ID + ":/oauth2/redirect")
        )
        return builder
            .setScopes(*SCOPES)
            .setLoginHint(email)
            .setUiLocales(locale)
            .build()
    }

    suspend fun authenticate(authResponse: AuthorizationResponse): Credentials {
        val authState = AuthState(authResponse, null)       // authorization code must not be stored; exchange it to refresh token
        val credentials = CompletableDeferred<Credentials>()

        withContext(Dispatchers.IO) {
            authService.performTokenRequest(authResponse.createTokenExchangeRequest()) { tokenResponse: TokenResponse?, refreshTokenException: AuthorizationException? ->
                Logger.log.info("Refresh token response: ${tokenResponse?.jsonSerializeString()}")

                if (tokenResponse != null) {
                    // success, save authState (= refresh token)
                    authState.update(tokenResponse, refreshTokenException)
                    credentials.complete(Credentials(authState = authState))
                }
            }
        }

        return credentials.await()
    }

}