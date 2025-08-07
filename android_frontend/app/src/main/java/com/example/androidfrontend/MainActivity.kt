package com.example.androidfrontend

import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androidfrontend.databinding.ActivityMainBinding

// PUBLIC_INTERFACE
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Game state
    private var board: Array<IntArray> = Array(3) { IntArray(3) { EMPTY } }
    private var currentPlayer = X
    private var xScore = 0
    private var oScore = 0
    private var drawScore = 0

    // Persistent storage for scores
    private lateinit var prefs: SharedPreferences

    // Player marker (default X as king)
    private var userSelected = X

    // Icons (use unicode as fallback for playful king/queen)
    private val king = "♔"
    private val queen = "♛"

    companion object {
        const val BOARD_SIZE = 3
        const val EMPTY = 0
        const val X = 1
        const val O = 2
        const val PREFS = "royal_ttt_prefs"
        const val X_SCORE_KEY = "x_score"
        const val O_SCORE_KEY = "o_score"
        const val DRAW_SCORE_KEY = "draw_score"
        const val PLAYER_KEY = "player_choice"
        const val ANIM_DURATION = 350L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        loadScores()
        setupBoardUI()
        updateScoreUI()
        updateTurnUI()

        // Action buttons
        binding.buttonRestart.setOnClickListener {
            showRestartDialog()
        }
        binding.buttonHelp.setOnClickListener {
            showHelpDialog()
        }
        binding.buttonPlayer.setOnClickListener {
            showPlayerSelectionDialog()
        }
    }

    // Helpers for persistent score
    private fun loadScores() {
        xScore = prefs.getInt(X_SCORE_KEY, 0)
        oScore = prefs.getInt(O_SCORE_KEY, 0)
        drawScore = prefs.getInt(DRAW_SCORE_KEY, 0)
        userSelected = prefs.getInt(PLAYER_KEY, X) // Default X
    }
    private fun saveScores() {
        prefs.edit()
            .putInt(X_SCORE_KEY, xScore)
            .putInt(O_SCORE_KEY, oScore)
            .putInt(DRAW_SCORE_KEY, drawScore)
            .putInt(PLAYER_KEY, userSelected)
            .apply()
    }

    private fun setupBoardUI() {
        // Iterate each cell in the 3x3 grid layout
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                val cell = getCellButton(row, col)
                cell.text = ""
                cell.isEnabled = true
                cell.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.md_theme_primaryContainer)
                )
                cell.setOnClickListener {
                    if (board[row][col] == EMPTY) {
                        makeMove(row, col, cell)
                    }
                }
            }
        }
        board = Array(3) { IntArray(3) { EMPTY } }
    }

    private fun getCellButton(row: Int, col: Int): Button {
        // Cell IDs: cell00, cell01, ..., cell22
        val cellId = resources.getIdentifier("cell$row$col", "id", packageName)
        return findViewById(cellId)
    }

    private fun makeMove(row: Int, col: Int, cell: Button) {
        if (board[row][col] != EMPTY) return
        board[row][col] = currentPlayer
        animateMove(cell, currentPlayer)
        cell.isEnabled = false

        // Check state
        val winner = getWinner()
        when (winner) {
            X -> {
                xScore++
                saveScores()
                endGame(winner = X)
            }
            O -> {
                oScore++
                saveScores()
                endGame(winner = O)
            }
            -1 -> {
                drawScore++
                saveScores()
                endGame(winner = -1)
            }
            else -> {
                // Continue game
                currentPlayer = if (currentPlayer == X) O else X
                updateTurnUI()
            }
        }
    }

    private fun animateMove(cell: Button, player: Int) {
        // PUBLIC_INTERFACE
        /** Animate tile with playful effect and set marker icon. */
        cell.text = if (player == X) king else queen
        cell.textSize = 32f
        val color = when (player) {
            X -> ContextCompat.getColor(this, R.color.md_theme_secondary)
            O -> ContextCompat.getColor(this, R.color.md_theme_primary)
            else -> ContextCompat.getColor(this, R.color.md_theme_outline)
        }
        cell.setTextColor(color)
        // Bounce scale animation
        cell.scaleX = 0.1f
        cell.scaleY = 0.1f
        val scaleX = ObjectAnimator.ofFloat(cell, "scaleX", 1f)
        val scaleY = ObjectAnimator.ofFloat(cell, "scaleY", 1f)
        scaleX.duration = ANIM_DURATION
        scaleY.duration = ANIM_DURATION
        scaleX.interpolator = BounceInterpolator()
        scaleY.interpolator = BounceInterpolator()
        scaleX.start()
        scaleY.start()
    }

    private fun updateScoreUI() {
        binding.scorePanel.text =
            "${king} $xScore    ${queen} $oScore    Draws $drawScore"
    }

    private fun updateTurnUI() {
        // UI accent color for player
        val turnIcon = if (currentPlayer == X) king else queen
        val name = if (currentPlayer == X) "King" else "Queen"
        val color = if (currentPlayer == X)
            getColor(R.color.md_theme_secondary)
        else
            getColor(R.color.md_theme_primary)
        binding.turnIndicator.text = "$turnIcon $name's turn"
        binding.turnIndicator.setTextColor(color)
    }

    private fun getWinner(): Int {
        // Returns X, O, -1(if draw), 0 if no winner yet
        // Rows and columns
        for (i in 0 until 3) {
            if (board[i][0] != EMPTY &&
                board[i][0] == board[i][1] && board[i][1] == board[i][2]
            ) {
                return board[i][0]
            }
            if (board[0][i] != EMPTY &&
                board[0][i] == board[1][i] && board[1][i] == board[2][i]
            ) {
                return board[0][i]
            }
        }
        // Diagonals
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2])
            return board[0][0]
        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0])
            return board[0][2]
        // Draw
        var full = true
        for (row in board) for (cell in row) if (cell == EMPTY) full = false
        return if (full) -1 else 0
    }

    private fun endGame(winner: Int) {
        // Show game result, disable board, update scores
        val msg = when (winner) {
            X -> "🎉 The King wins!"
            O -> "🎉 The Queen wins!"
            -1 -> "It's a Draw!"
            else -> ""
        }
        for (row in 0 until BOARD_SIZE)
            for (col in 0 until BOARD_SIZE)
                getCellButton(row, col).isEnabled = false
        updateScoreUI()
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("$msg\nRestart or choose player to play again.")
            .setPositiveButton("Restart") { _, _ ->
                restartGame()
            }
            .setNegativeButton("Change Player") { _, _ ->
                showPlayerSelectionDialog()
            }
            .setNeutralButton("OK", null)
            .show()
    }

    private fun showRestartDialog() {
        AlertDialog.Builder(this)
            .setTitle("Restart Game")
            .setMessage("Restart the current game?\nCurrent scores remain.")
            .setPositiveButton("Yes") { _, _ -> restartGame() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun restartGame() {
        board = Array(3) { IntArray(3) { EMPTY } }
        currentPlayer = userSelected
        setupBoardUI()
        updateScoreUI()
        updateTurnUI()
    }

    private fun showPlayerSelectionDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_player, null)
        builder.setView(view)
        val kingBtn = view.findViewById<RadioButton>(R.id.radio_king)
        val queenBtn = view.findViewById<RadioButton>(R.id.radio_queen)
        if (userSelected == X) kingBtn.isChecked = true else queenBtn.isChecked = true

        builder.setTitle("Choose Your Player")
            .setPositiveButton("OK") { _, _ ->
                userSelected = if (kingBtn.isChecked) X else O
                currentPlayer = userSelected
                saveScores()
                restartGame()
            }
            .setNegativeButton("Cancel", null)
        builder.create().show()
    }

    private fun showHelpDialog() {
        // Launch HelpActivity for a fullpage experience
        startActivity(HelpActivity.newIntent(this))
    }
}
