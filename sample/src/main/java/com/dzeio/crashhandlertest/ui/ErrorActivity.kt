package com.dzeio.crashhandlertest.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.dzeio.crashhandlertest.Application
import com.dzeio.crashhandlertest.databinding.ActivityErrorBinding
import kotlin.system.exitProcess

class ErrorActivity : AppCompatActivity() {

    companion object {
        const val TAG = "${Application.TAG}/ErrorActivity"
    }

    private lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getStringExtra("error")

        // Get Application datas
        val deviceToReport = if (Build.DEVICE.contains(Build.MANUFACTURER)) Build.DEVICE else "${Build.MANUFACTURER} ${Build.DEVICE}"

        val reportText = """
            Crash Report (Thread: ${intent?.getLongExtra("threadId", -1) ?: "unknown"})
            on $deviceToReport (${Build.MODEL}) running Android ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})
            
            backtrace:
            
        """.trimIndent() + data

        // put it in the textView
        binding.errorText.text = reportText

        // Handle the Quit button
        binding.errorQuit.setOnClickListener {
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }

        // Handle the Email Button
        binding.errorSubmitEmail.setOnClickListener {

            // Create Intent
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:")
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("report.openhealth@dzeio.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Error report for application crash")
            intent.putExtra(Intent.EXTRA_TEXT, "Send Report Email\n$reportText")

            try {
                startActivity(Intent.createChooser(intent, "Send Report Email..."))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Not Email client found :(", Toast.LENGTH_LONG).show()
            }
        }

        // Handle the GitHub Button
        binding.errorSubmitGithub.setOnClickListener {

            // Build URL
            val url = "https://github.com/dzeiocom/OpenHealth/issues/new?title=Application Error&body=$reportText"

            try {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No Web Browser found :(", Toast.LENGTH_LONG).show()
            }
        }
    }
}
