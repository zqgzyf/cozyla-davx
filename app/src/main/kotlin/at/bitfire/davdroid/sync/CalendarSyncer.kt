/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.sync

import android.accounts.Account
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.provider.CalendarContract
import at.bitfire.davdroid.db.AppDatabase
import at.bitfire.davdroid.db.Collection
import at.bitfire.davdroid.db.Service
import at.bitfire.davdroid.log.Logger
import at.bitfire.davdroid.network.HttpClient
import at.bitfire.davdroid.resource.LocalCalendar
import at.bitfire.davdroid.settings.AccountSettings
import at.bitfire.ical4android.AndroidCalendar
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.util.logging.Level
import javax.inject.Inject

/**
 * Sync logic for calendars
 */
class CalendarSyncer @Inject constructor(
    @ApplicationContext context: Context,
    db: AppDatabase,
    private val calendarSyncManagerFactory: CalendarSyncManager.Factory
): Syncer(context, db) {

    override fun sync(
        account: Account,
        extras: Array<String>,
        authority: String,
        httpClient: Lazy<HttpClient>,
        provider: ContentProviderClient,
        syncResult: SyncResult
    ) {
        val accountSettings = AccountSettings(context, account)

        if (accountSettings.getEventColors())
            AndroidCalendar.insertColors(provider, account)
        else
            AndroidCalendar.removeColors(provider, account)

        updateLocalCalendars(provider, account, accountSettings)

        val calendars = AndroidCalendar
            .find(account, provider, LocalCalendar.Factory, "${CalendarContract.Calendars.SYNC_EVENTS}!=0", null)
        for (calendar in calendars) {
            Logger.log.info("Synchronizing calendar #${calendar.id}, URL: ${calendar.name}")

            val syncManager = calendarSyncManagerFactory.calendarSyncManager(account, accountSettings, extras, httpClient.value, authority, syncResult, calendar)
            syncManager.performSync()
        }
    }

    private fun updateLocalCalendars(provider: ContentProviderClient, account: Account, settings: AccountSettings) {
        val service = db.serviceDao().getByAccountAndType(account.name, Service.TYPE_CALDAV)

        val remoteCalendars = mutableMapOf<HttpUrl, Collection>()
        if (service != null)
            for (collection in db.collectionDao().getSyncCalendars(service.id)) {
                remoteCalendars[collection.url] = collection
            }

        // delete/update local calendars
        val updateColors = settings.getManageCalendarColors()
        for (calendar in AndroidCalendar.find(account, provider, LocalCalendar.Factory, null, null))
            calendar.name?.let {
                val url = it.toHttpUrl()
                val info = remoteCalendars[url]
                if (info == null) {
                    Logger.log.log(Level.INFO, "Deleting obsolete local calendar", url)
                    calendar.delete()
                } else {
                    // remote CollectionInfo found for this local collection, update data
                    Logger.log.log(Level.FINE, "Updating local calendar $url", info)
                    calendar.update(info, updateColors)
                    // we already have a local calendar for this remote collection, don't take into consideration anymore
                    remoteCalendars -= url
                }
            }

        // create new local calendars
        for ((_, info) in remoteCalendars) {
            Logger.log.log(Level.INFO, "Adding local calendar", info)
            LocalCalendar.create(account, provider, info)
        }
    }
}