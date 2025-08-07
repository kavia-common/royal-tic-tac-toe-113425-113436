package com.example.androidfrontend

import android.view.View
import android.widget.Button
import android.widget.TextView

// This is a simplified, generated stub for ActivityMainBinding for view access.
// Normally, Android Studio auto-generates this. For code generation, we define it for correct reference.
class ActivityMainBinding private constructor(val root: View) {
    val scorePanel: TextView = root.findViewById(R.id.score_panel)
    val turnIndicator: TextView = root.findViewById(R.id.turn_indicator)
    val buttonRestart: Button = root.findViewById(R.id.button_restart)
    val buttonHelp: Button = root.findViewById(R.id.button_help)
    val buttonPlayer: Button = root.findViewById(R.id.button_player)
    val cell00: Button = root.findViewById(R.id.cell00)
    val cell01: Button = root.findViewById(R.id.cell01)
    val cell02: Button = root.findViewById(R.id.cell02)
    val cell10: Button = root.findViewById(R.id.cell10)
    val cell11: Button = root.findViewById(R.id.cell11)
    val cell12: Button = root.findViewById(R.id.cell12)
    val cell20: Button = root.findViewById(R.id.cell20)
    val cell21: Button = root.findViewById(R.id.cell21)
    val cell22: Button = root.findViewById(R.id.cell22)

    companion object {
        fun inflate(inflater: android.view.LayoutInflater): ActivityMainBinding {
            val root = inflater.inflate(R.layout.activity_main, null, false)
            return ActivityMainBinding(root)
        }
    }
}
