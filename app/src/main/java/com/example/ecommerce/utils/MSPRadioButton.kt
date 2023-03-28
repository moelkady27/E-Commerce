package com.example.ecommerce.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView

class MSPRadioButton(context: Context, attrs: AttributeSet) : AppCompatRadioButton(context,attrs) {
    init {
        applyFont()
    }

    private fun applyFont(){
        val typeface : Typeface =
            Typeface.createFromAsset(context.assets , "montserrat.bold.ttf")
        setTypeface(typeface)
    }
}