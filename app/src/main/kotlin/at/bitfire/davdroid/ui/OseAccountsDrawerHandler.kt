/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import at.bitfire.davdroid.Constants
import at.bitfire.davdroid.Constants.COMMUNITY_URL
import at.bitfire.davdroid.Constants.FEDIVERSE_URL
import at.bitfire.davdroid.Constants.MANUAL_URL
import at.bitfire.davdroid.Constants.withStatParams
import at.bitfire.davdroid.R
import javax.inject.Inject

open class OseAccountsDrawerHandler @Inject constructor(): AccountsDrawerHandler() {

    companion object {
        const val WEB_CONTEXT = "AccountsDrawerHandler"
    }

    @Composable
    override fun MenuEntries(
        snackbarHostState: SnackbarHostState
    ) {
        val uriHandler = LocalUriHandler.current

        // Most important entries
        ImportantEntries(snackbarHostState)





        // External links
        MenuHeading(R.string.navigation_drawer_external_links)

        MenuEntry(
            icon = Icons.Default.Home,
            title = stringResource(R.string.navigation_drawer_cozyla_store),
            onClick = {
                uriHandler.openUri(Constants.COZYLA_STORE_URL
                    .buildUpon()
//                    .withStatParams(WEB_CONTEXT)
                    .build().toString())
            }
        )

        MenuEntry(
            icon = Icons.Default.Info,
            title = stringResource(R.string.navigation_drawer_manual),
            onClick = {
                uriHandler.openUri(MANUAL_URL.toString())
            }
        )

        MenuEntry(
            icon = Icons.AutoMirrored.Default.HelpCenter,
            title = stringResource(R.string.navigation_drawer_faq),
            onClick = {
                uriHandler.openUri(
                    Constants.COZYLA_FAQ_URL.buildUpon()
//                        .withStatParams(WEB_CONTEXT)
                        .build().toString()
                )
            }
        )

        MenuEntry(
            icon = Icons.Default.VideoLibrary,
            title = stringResource(R.string.navigation_drawer_tutorial_video),
            onClick = {
                uriHandler.openUri(Constants.TUTORIAL_VIDEOS
                    .buildUpon()
                    .build().toString())
            }
        )
        MenuEntry(
            icon = Icons.Default.SupportAgent,
            title = stringResource(R.string.navigation_drawer_support),
            onClick = {
                uriHandler.openUri(Constants.COZYLA_SUPPORT_URL.toString())
            }
        )

        MenuEntry(
            icon = Icons.Default.CloudOff,
            title = stringResource(R.string.navigation_drawer_privacy_policy),
            onClick = {
                uriHandler.openUri(
                    Constants.COZYLA_PRIVACY.buildUpon()
                        .build().toString()
                )
            }
        )
    }

    @Composable
    @Preview
    fun MenuEntries_Standard_Preview() {
        Column {
            MenuEntries(SnackbarHostState())
        }
    }


    @Composable
    open fun Contribute(onContribute: () -> Unit) {
        MenuEntry(
            icon = Icons.Default.VolunteerActivism,
            title = stringResource(R.string.navigation_drawer_contribute),
            onClick = onContribute
        )
    }

}