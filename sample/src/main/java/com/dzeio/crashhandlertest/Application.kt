package com.dzeio.crashhandlertest

import androidx.preference.PreferenceManager
import com.dzeio.crashhandler.CrashHandler
import com.dzeio.crashhandlertest.ui.ErrorActivity

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        // get the device Preference store
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // create the Crash Handler
        CrashHandler.Builder()
            // need the application context to run
            .withContext(this)
            // define a custom activity to use
            .withActivity(ErrorActivity::class.java)
            // define the preferenceManager to be able to handle crash in a custom Activity and to have the previous crash date in the logs
            .withPrefs(prefs)
            .withPrefsKey("com.dzeio.crashhandler.key")
            // a Prefix to add at the beginning the crash message
            .withPrefix(
                "POKEMON"
            )
            // a Suffix to add at the end of the crash message
            .withSuffix("WHYYYYY")
            // build & start the module
            .build().setup()
    }
}
