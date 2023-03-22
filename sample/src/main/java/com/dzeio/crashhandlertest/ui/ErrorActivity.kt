package com.dzeio.crashhandlertest.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.dzeio.crashhandlertest.databinding.ActivityErrorBinding
import kotlin.system.exitProcess

/**
 * Example Activity for a custom ErrorActivity
 *
 * note: try to keep the complexity of this class as low as possible
 * to make sure this will always load
 */
class ErrorActivity : AppCompatActivity() {

    // the view binding
    private lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // inflate the view
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get the error string from the library
        val data = intent.getStringExtra("error")

        // put it in the textView
        binding.errorText.text = data

        // Handle the Quit button
        binding.errorQuit.setOnClickListener {
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }

        // Handle the Email Button
        binding.errorSubmitEmail.setOnClickListener {
            // Create Intent
            val intent = Intent(Intent.ACTION_SEND)
            intent.setDataAndType(Uri.parse("mailto:"), "text/plain")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("report.openhealth@dzeio.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Error report for application crash")
            intent.putExtra(Intent.EXTRA_TEXT, "Send Report Email\n$data")

            // send intent
            try {
                startActivity(Intent.createChooser(intent, "Send Report Email..."))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Not Email client found :(", Toast.LENGTH_LONG).show()
            }
        }

        // Handle the GitHub Button
        binding.errorSubmitGithub.setOnClickListener {
            // Build URL
            val title = "Application Error"
            val url = "https://github.com/dzeiocom/OpenHealth/issues/new?title=$title&body=$data"

            // send intent
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
