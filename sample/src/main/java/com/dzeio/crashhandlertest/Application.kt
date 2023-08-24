package com.dzeio.crashhandlertest

import androidx.preference.PreferenceManager
import com.dzeio.crashhandler.CrashHandler
import com.dzeio.crashhandlertest.ui.ErrorActivity
import java.io.File

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        // get the device Preference store
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // create the Crash Handler
        CrashHandler.Builder()
            // need the application context to run
            .withContext(this)
            // every other items below are optionnal
            // define a custom activity to use
            .withActivity(ErrorActivity::class.java)
            // define the preferenceManager to have the previous crash date in the logs
            .withPrefs(prefs)
            .withPrefsKey("com.dzeio.crashhandler.key")
            // a Prefix to add at the beginning the crash message
            .withPrefix("Prefix")
            // a Suffix to add at the end of the crash message
            .withSuffix("Suffix")
            // add a location where the crash logs are also exported (can be recovered as a zip ByteArray by calling {CrashHandler.getInstance().export()})
            .withExportLocation(
                File(this.getExternalFilesDir(null) ?: this.filesDir, "crash-logs")
            )
            // build & start the module
            .build().setup()
    }
}
