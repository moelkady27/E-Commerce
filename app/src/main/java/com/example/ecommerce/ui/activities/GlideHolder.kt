package com.example.ecommerce.ui.activities

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.IOException

class GlideHolder (val context: Context){

    fun loadUserPicture(image : Any , imageView: ImageView){
        try {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(imageView)
        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun loadProductPicture(image : Any , imageView: ImageView){
        try {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .into(imageView)
        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }
}