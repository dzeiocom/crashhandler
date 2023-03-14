package com.dzeio.crashhandlertest

import com.dzeio.crashhandler.CrashHandler
import com.dzeio.crashhandlertest.ui.ErrorActivity

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        CrashHandler.Builder()
            .withActivity(ErrorActivity::class.java)
            .withContext(this)
            .withPrefix("Pouet :D")
            .withSuffix("WHYYYYY")
            .build().setup()
    }
}
