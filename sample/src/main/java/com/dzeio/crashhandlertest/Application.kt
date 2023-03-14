package com.dzeio.crashhandlertest

import androidx.preference.PreferenceManager
import com.dzeio.crashhandler.CrashHandler
import com.dzeio.crashhandlertest.ui.ErrorActivity

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        CrashHandler.Builder()
            .withActivity(ErrorActivity::class.java)
            .withContext(this)
            .withPrefs(prefs)
            .withPrefsKey("com.dzeio.crashhandler.key")
            .withPrefix("Pouet :D")
            .withSuffix("WHYYYYY")
            .build().setup()
    }
}
