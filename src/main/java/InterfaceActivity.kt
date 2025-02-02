package com.example.thirty

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import kotlin.random.Random

object ViewUtils {

    // Array to hold the dice images
    private val imageResources = arrayOf(
        R.drawable.white1, // Dice side 1
        R.drawable.white2, // Dice side 2
        R.drawable.white3, // Dice side 3
        R.drawable.white4, // Dice side 4
        R.drawable.white5, // Dice side 5
        R.drawable.white6, // Dice side 6
    )

    //Translate the pictures into their integer values
    val diceImageMap = mapOf(
        R.drawable.white1 to 1,
        R.drawable.white2 to 2,
        R.drawable.white3 to 3,
        R.drawable.white4 to 4,
        R.drawable.white5 to 5,
        R.drawable.white6 to 6
    )

    // Function to create a GridLayout for the dice
    fun createGridLayout(context: Context): GridLayout {
        return GridLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            rowCount = 3
            columnCount = 2
        }
    }

    // Function to create an ImageView for a dice
    fun createDiceImageView(context: Context): ImageView {
        return ImageView(context).apply {
            setImageResource(getRandomImage())
            layoutParams = GridLayout.LayoutParams().apply {
                width = 400
                height = 400
                topMargin = 15
                bottomMargin = 15
                leftMargin = 15
                rightMargin = 15
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = getDiceBackground(false) // Default border
        }
    }

    // Function to create a Button to roll selected dice
    @SuppressLint("SetTextI18n")
    fun createRollButton(context: Context): Button {
        return Button(context).apply {
            text = "Throw"
            textSize = 24f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = 50
            }
            setPadding(40, 20, 40, 20)
            minimumWidth = 400
            minimumHeight = 150

            // Create a rounded background
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 50f
                setColor(Color.WHITE)
                setStroke(5, Color.BLACK)
            }
        }
    }

    // Function to create a Score Button
    @SuppressLint("SetTextI18n")
    fun createChooseButton(context: Context): Button {
        return Button(context).apply {
            text = "Choose"
            textSize = 24f // Increase text size

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = 50
            }

            setPadding(40, 20, 40, 20) // Increase padding
            minimumWidth = 400
            minimumHeight = 150

            // Create a rounded background
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 50f // Curve the edges
                setColor(Color.WHITE) // Background color
                setStroke(5, Color.BLACK) // Black border
            }
        }
    }

    // Function to set dice selection border
    fun setDiceBorder(dice: ImageView, isSelected: Boolean) {
        dice.background = getDiceBackground(isSelected)
    }

    // Function to create dice background with border
    private fun getDiceBackground(isSelected: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 1f // Keep slightly rounded corners
            setColor(Color.TRANSPARENT) // Ensure background remains transparent

            // Set a much thicker border when selected
            val borderWidth = if (isSelected) 10 else 0 // Thicker when selected
            val borderColor = if (isSelected) Color.RED else Color.TRANSPARENT
            setStroke(borderWidth, borderColor)
        }
    }

    // Function to get a random dice image
    fun getRandomImage(): Int {
        return imageResources[Random.nextInt(imageResources.size)]
    }

    fun createSpinner(context: Context): Spinner {
        val spinner = Spinner(context)

        // Create an array adapter with 10 options
        val options = arrayOf(
            "Low", "4", "5",
            "6", "7", "8", "9", "10",
            "11", "12"
        )
        // Set spinner layout
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item, // Default spinner layout
            options
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Increase the spinner size
        spinner.layoutParams = RelativeLayout.LayoutParams(
            850,  // Width (increase for a bigger spinner)
            450   // Height (increase for a bigger spinner)
        )

        // Increase text size and padding inside the spinner
        spinner.setPadding(40, 40, 40, 40)
        spinner.setPopupBackgroundResource(android.R.color.white) // White background

        return spinner
    }
}
