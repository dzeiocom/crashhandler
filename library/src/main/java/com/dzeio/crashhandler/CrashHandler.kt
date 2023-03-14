package com.dzeio.crashhandler

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.dzeio.crashhandler.CrashHandler.Builder
import com.dzeio.crashhandler.ui.ErrorActivity
import java.util.Date
import kotlin.system.exitProcess

/**
 * the Crash Handler class, you can get an instance by using it's [Builder]
 */
class CrashHandler private constructor(
    private val application: Application?,
    private val activity: Class<*>,
    private val prefs: SharedPreferences?,
    private val prefsKey: String?,
    @StringRes
    private val errorReporterCrashKey: Int?,
    private val prefix: String? = null,
    private val suffix: String? = null
) {

    private companion object {
        private const val TAG = "CrashHandler"
    }

    /**
     * Builder for the crash handler
     */
    class Builder {
        private var application: Application? = null
        private var prefs: SharedPreferences? = null
        private var prefsKey: String? = null
        private var errorReporterCrashKey: Int? = null
        private var activity: Class<*>? = ErrorActivity::class.java
        private var prefix: String? = null
        private var suffix: String? = null

        /**
         * Change the Crash activity to with your own
         *
         * note: you can get the backtrace text by using `intent.getStringExtra("error")`
         *
         * @param context the context class to use
         */
        fun withContext(context: Context): Builder {
            this.application = context.applicationContext as Application?
            return this
        }

        /**
         * Change the Crash activity to with your own
         *
         * note: you can get the backtrace text by using `intent.getStringExtra("error")`
         *
         * @param activity the activity class to use
         */
        fun withActivity(activity: Class<*>): Builder {
            this.activity = activity
            return this
        }

        /**
         * [SharedPreferences] of your app to be able to handle ErrorActivity crashes
         *
         * note: you also need to use [withPrefsKey]
         *
         * @param prefs instance of [SharedPreferences] to use
         */
        fun withPrefs(prefs: SharedPreferences?): Builder {
            this.prefs = prefs
            return this
        }

        /**
         * the key of the [SharedPreferences] you want to let the library handle
         *
         * note: you also need to use [withPrefs]
         *
         * @param prefsKey the key to use
         */
        fun withPrefsKey(prefsKey: String?): Builder {
            this.prefsKey = prefsKey
            return this
        }

        /**
         * the resource key to use for the [Toast] if ErrorActivity crashed
         *
         * @param errorReporterCrashKey the string key to use
         */
        fun witheErrorReporterCrashKey(@StringRes errorReporterCrashKey: Int): Builder {
            this.errorReporterCrashKey = errorReporterCrashKey
            return this
        }

        /**
         * text to add after the "Crash report:" text and before the rest
         *
         * ex: "${BuildConfig.APPLICATION_ID} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
         *
         * @param prefix the text you add
         */
        fun withPrefix(prefix: String): Builder {
            this.prefix = prefix
            return this
        }

        /**
         * text to add after the content generated by the handler
         *
         * @param suffix the text
         */
        fun withSuffix(suffix: String): Builder {
            this.suffix = suffix
            return this
        }

        /**
         * build the Crash Handler
         */
        fun build(): CrashHandler {
            return CrashHandler(
                application,
                activity!!,
                prefs,
                prefsKey,
                errorReporterCrashKey,
                prefix,
                suffix
            )
        }
    }

    private var oldHandler: Thread.UncaughtExceptionHandler? = null

    fun setup() {
        if (application != null) {
            this.setup(application)
        }
    }

    /**
     * Destroy the handler
     */
    fun destroy() {
        if (oldHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(oldHandler)
        }
    }

    /**
     * Setup the crash handler, after this method is executed crashes should be handled through your
     * activity
     *
     * @param application the application instance to make sure everything is setup right
     */
    fun setup(application: Application) {
        // Application Error Handling
        oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->

            // Log error to logcat if it wasn't done before has it can not be logged depending on the version
            Log.e(TAG, "En error was detected", paramThrowable)

            // mostly unusable data but also log Thread Stacktrace
            Log.i(TAG, "Thread StackTrace:")
            for (item in paramThread.stackTrace) {
                Log.i(TAG, item.toString())
            }

            // get current time an date
            val now = Date().time

            // prepare to build debug string
            var data = "Crash report:\n\n"

            data += prefix ?: ""

            // add device informations
            val deviceToReport =
                if (Build.DEVICE.contains(Build.MANUFACTURER)) {
                    Build.DEVICE
                } else {
                    "${Build.MANUFACTURER} ${Build.DEVICE}"
                }

            data += "\n\non $deviceToReport (${Build.MODEL}) running Android ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})"

            // add the current time to it
            data += "\n\nCrash happened at ${Date(now)}"

            // if lib as access to the preferences store
            if (prefs != null && prefsKey != null) {
                // get the last Crash
                val lastCrash = prefs.getLong(prefsKey, 0L)

                // then add it to the logs :D
                data += "\nLast crash happened at ${Date(lastCrash)}"

                // if a crash already happened just before it means the Error Activity crashed lul
                if (lastCrash >= now - 1000) {
                    // log it :D
                    Log.e(
                        TAG,
                        "Seems like the ErrorActivity also crashed, letting the OS handle it"
                    )

                    // try to send a toast indicating it
                    Toast.makeText(
                        application,
                        errorReporterCrashKey ?: R.string.error_reporter_crash,
                        Toast.LENGTH_LONG
                    ).show()

                    // Use the default exception handler
                    oldHandler?.uncaughtException(paramThread, paramThrowable)
                    return@setDefaultUncaughtExceptionHandler
                }

                // update the store
                prefs.edit().putLong(prefsKey, now).apply()
            }

            Log.i(TAG, "Collecting Error")

            // get Thread name and ID
            data += "\n\nHappened on Thread \"${paramThread.name}\" (${paramThread.id})"

            // print exception backtrace
            data += "\n\nException:\n${paramThrowable.stackTraceToString()}\n\n"

            data += suffix ?: ""

            Log.i(TAG, "Starting ${activity.name}")

            // prepare the activity
            val intent = Intent(application, activity)

            // add flags so that it don't use the current Application context
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )

            // add the Data String
            intent.putExtra("error", data)

            // Start the activity
            application.startActivity(intent)
            Log.i(TAG, "Activity should have started")

            // Kill self
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }
    }
}
