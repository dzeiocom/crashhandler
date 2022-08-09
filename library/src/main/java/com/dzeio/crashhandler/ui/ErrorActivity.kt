package com.dzeio.crashhandler.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dzeio.crashhandler.databinding.CrashHandlerActivityErrorBinding

class ErrorActivity : AppCompatActivity() {

    private lateinit var binding: CrashHandlerActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CrashHandlerActivityErrorBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val data = intent.getStringExtra("error")

        // put it in the textView
        binding.errorText.apply {
            text = data
            setTextIsSelectable(true)
        }

        // Handle the Quit button
        binding.errorQuit.setOnClickListener {
            finishAffinity()
        }

        // handle copy to clipboard button
        binding.copyToClipboard.setOnClickListener {
            val clipboard =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("error backtrace", data)
            clipboard.setPrimaryClip(clip)
        }

    }
}
