package com.dzeio.crashhandlertest

import com.dzeio.crashhandler.CrashHandler

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        CrashHandler.Builder()
            .withPrefix("Pouet :D")
            .withSuffix("WHYYYYY")
            .build().setup(this)
    }
}