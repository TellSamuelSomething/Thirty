package com.example.thirty

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val gameLogic = Logic() // Game logic instance
    private var rollCount = 0 // Track number of rolls
    private lateinit var scoreTextView: TextView // Display total score
    private lateinit var chooseButton: Button // Choose button
    private lateinit var spinner: Spinner // Category selection
    private lateinit var adapter: ArrayAdapter<String> // Spinner adapter
    private val usedCategories = mutableSetOf<String>() // Track used categories

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the root RelativeLayout with a blue background
        val rootLayout = RelativeLayout(this).apply {
            setBackgroundColor(Color.parseColor("#ADD8E6"))
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Create dice grid
        val gridLayout = ViewUtils.createGridLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
        }

        // Create roll button
        val rollButton = ViewUtils.createRollButton(this).apply {
            isEnabled = true
        }
        rollButton.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.ALIGN_PARENT_END)
            bottomMargin = 200
            rightMargin = 70
        }

        // Create the spinner (category selection)
        spinner = ViewUtils.createSpinner(this)
        spinner.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.ALIGN_PARENT_START)
            bottomMargin = 200
            leftMargin = 70
        }

        // Create choose button
        chooseButton = ViewUtils.createChooseButton(this).apply {
            isEnabled = true // Initially disabled until dice is rolled
        }
        chooseButton.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.CENTER_HORIZONTAL)
            bottomMargin = 200
        }

        // Create score display
        scoreTextView = TextView(this).apply {
            text = "Score: 0"
            textSize = 28f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_TOP)
                topMargin = 500
            }
        }

        // Track selected dice
        val selectedDice = mutableSetOf<ImageView>()
        val diceList = mutableListOf<ImageView>()

        // Create dice images
        for (i in 1..6) {
            val dice = ViewUtils.createDiceImageView(this)
            diceList.add(dice)
            gridLayout.addView(dice)

            // Toggle selection on click
            dice.setOnClickListener {
                if (selectedDice.contains(dice)) {
                    selectedDice.remove(dice)
                    ViewUtils.setDiceBorder(dice, false)
                } else {
                    selectedDice.add(dice)
                    ViewUtils.setDiceBorder(dice, true)
                }
            }
        }

        // Set roll button click listener
        rollButton.setOnClickListener {
            if (rollCount < 2 && selectedDice.isNotEmpty()) {
                selectedDice.forEach { it.setImageResource(ViewUtils.getRandomImage()) }
                rollCount++

                if (rollCount == 2) {
                    rollButton.isEnabled = false // Disable after 2 rolls
                }
            }
        }

        // Set choose button click listener
        // When choose button has been pressed one spinner option choice will be used up and the score for that choice will
        // appear in a pop-up text and in the score text above the dice.
        chooseButton.setOnClickListener {
            val selectedCategory = spinner.selectedItem.toString()

            val diceValues = diceList.map { dice ->
                val drawable = dice.drawable.constantState
                val diceNumber = ViewUtils.diceImageMap.entries.firstOrNull { (imageRes, _) ->
                    resources.getDrawable(imageRes, null).constantState == drawable
                }?.value ?: 0
                diceNumber
            }

            // Calculate score
            val roundScore = gameLogic.calculateScore(diceValues, selectedCategory)
            Toast.makeText(this, "Scored: $roundScore", Toast.LENGTH_SHORT).show()

            // Update total score
            scoreTextView.text = "Score: ${gameLogic.getTotalScore()}"

            // Disable selected category
            usedCategories.add(selectedCategory)
            updateSpinnerOptions()

            // Reset round
            rollCount = 0 // Set throw count to 0
            rollButton.isEnabled = true // Reset throw button
            diceList.forEach { it.setImageResource(ViewUtils.getRandomImage()) } //Throw all dice for the next round
            selectedDice.forEach { dice -> ViewUtils.setDiceBorder(dice, false) } //Remove all selected dice
            selectedDice.clear() //Clear selected dice list for next round

            // Check if game is over
            if (isGameOver()) {
                showGameOverDialog()
            }
        }

        // Add views to root layout
        rootLayout.addView(gridLayout)
        rootLayout.addView(spinner)
        rootLayout.addView(rollButton)
        rootLayout.addView(chooseButton)
        rootLayout.addView(scoreTextView)

        setContentView(rootLayout)
    }

    // Disable used categories in spinner
    // This function updates the spinner after each choice by removing each option after usage.
    private fun updateSpinnerOptions() {
        val availableOptions = mutableListOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        availableOptions.removeAll(usedCategories)

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, availableOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        runOnUiThread {
            spinner.adapter = adapter
        }
    }

    // Show game-over dialog
    // This function shows the dialog with the total score and all individual scores for each score section.
    private fun showGameOverDialog() {
        val totalScore = gameLogic.getTotalScore()

        AlertDialog.Builder(this)
            .setTitle("Game Finished")
            .setMessage("Total Score: $totalScore\n" +
                    "Low score: ${gameLogic.scoreMap["Low"] ?: "N/A"}\n" +
                    "4 Score: ${gameLogic.scoreMap["4"] ?: "N/A"}\n" +
                    "5 Score: ${gameLogic.scoreMap["5"] ?: "N/A"}\n" +
                    "6 Score: ${gameLogic.scoreMap["6"] ?: "N/A"}\n" +
                    "7 Score: ${gameLogic.scoreMap["7"] ?: "N/A"}\n" +
                    "8 Score: ${gameLogic.scoreMap["8"] ?: "N/A"}\n" +
                    "9 Score: ${gameLogic.scoreMap["9"] ?: "N/A"}\n" +
                    "10 Score: ${gameLogic.scoreMap["10"] ?: "N/A"}\n" +
                    "11 Score: ${gameLogic.scoreMap["11"] ?: "N/A"}\n" +
                    "12 Score: ${gameLogic.scoreMap["12"] ?: "N/A"}\n")
            .setPositiveButton("OK") { _, _ -> resetGame() }
            .setCancelable(false)
            .show()
    }

    // Reset the game
    // This function resets all the games numbers and categories.
    @SuppressLint("SetTextI18n")
    private fun resetGame() {
        gameLogic.gameIsOver()
        usedCategories.clear()
        updateSpinnerOptions()
        scoreTextView.text = "Score: ${gameLogic.getTotalScore()}"
        rollCount = 0
    }
    //Function that checks if the game is over. Like when all choices in the spinner has been picked once each.
    private fun isGameOver():Boolean {
        return usedCategories.size == 10
    }
}
