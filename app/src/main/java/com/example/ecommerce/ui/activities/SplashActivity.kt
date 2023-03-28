package com.example.ecommerce.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.ecommerce.R
import com.example.ecommerce.firestore.FireStoreClass
import kotlinx.android.synthetic.main.activity_splash.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({

            var currentUserID = FireStoreClass().getCurrentUserID()

            if (currentUserID.isEmpty()){
                startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
            }
            else{
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
            }

//                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }, 3000)

    }
}