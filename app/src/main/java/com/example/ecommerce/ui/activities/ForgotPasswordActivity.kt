package com.example.ecommerce.ui.activities

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.example.ecommerce.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_forgot_password_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24)
        }

        toolbar_forgot_password_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        btn_submit.setOnClickListener {
            forgotPassword()
        }
    }

    private fun forgotPassword(){
        val email: String =et_email_forgot_password.text.toString().trim()
        if (email.isEmpty()){
            showErrorSnackBar("please enter your email",true)
        }
        else{
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener {
                        task ->

                    hideProgressDialog()
                    if (task.isSuccessful) {

                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "E_Mail sent success.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
        }
    }
}