package com.dzeio.crashhandlertest.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.dzeio.crashhandler.CrashHandler
import com.dzeio.crashhandlertest.R
import com.dzeio.crashhandlertest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /**
     * Response to the export button event
     */
    private val writeResult = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) {

        val zipFile = CrashHandler.getInstance().export()
        if (zipFile == null) {
            return@registerForActivityResult
        }

        // write file to location
        this.contentResolver.openOutputStream(it!!)?.apply {
            write(zipFile)
            close()
        }

        Toast.makeText(
            this,
            R.string.export_complete,
            Toast.LENGTH_LONG
        ).show()
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonFirst.setOnClickListener {
            // DIE
            throw Exception(getString(R.string.error_message))
        }

        binding.buttonExport.setOnClickListener {
            // launch the popin to select where to save the file
            writeResult.launch("output.zip")
        }

        binding.buttonClearExports.setOnClickListener {
            // clear the handler exports
            CrashHandler.getInstance().clearExports()
        }
    }
}
