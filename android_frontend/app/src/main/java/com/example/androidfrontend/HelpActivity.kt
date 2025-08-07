package com.example.androidfrontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// PUBLIC_INTERFACE
class HelpActivity : AppCompatActivity() {
    companion object {
        // PUBLIC_INTERFACE
        fun newIntent(context: Context) = Intent(context, HelpActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        // Optionally, set explanatory text programmatically or let the XML handle it.
    }
}
