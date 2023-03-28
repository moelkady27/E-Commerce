package com.example.ecommerce.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.example.ecommerce.R
import com.example.ecommerce.firestore.FireStoreClass
import com.example.ecommerce.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        tv_login.setOnClickListener {
            onBackPressed()
        }
        btn_register.setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_register_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }

        toolbar_register_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }


private fun validateRegisterDetails(): Boolean {
    var isValid = true

    val firstName = et_first_name.text.toString()
    val lastName = et_last_name.text.toString()
    val email = et_email_id.text.toString()
    val password = et_password1.text.toString()
    val confirmPassword = et_confirm_password.text.toString()
    val termsAndConditions = cb_terms_and_condition.text.toString()

    if (firstName.isEmpty()) {
        showErrorSnackBar("Please enter your first name",true)
        isValid = false
    }

    if (lastName.isEmpty()) {
        showErrorSnackBar("Please enter your last name",true)
        isValid = false
    }

    if (email.isEmpty()) {
        showErrorSnackBar("Please enter your email",true)
        isValid = false
    }
    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        showErrorSnackBar("Please enter a valid email address",true)
        isValid = false
    }

    if (password.isEmpty()) {
        showErrorSnackBar("Please enter a password",true)
        isValid = false
    } else if (password.length < 8) {
        showErrorSnackBar("Password must be at least 8 characters long",true)
        isValid = false
    }

    if (confirmPassword.isEmpty()) {
        showErrorSnackBar("Please confirm your password",true)
        isValid = false
    }
    else if (confirmPassword != password) {
        showErrorSnackBar("Passwords and confirm password must be same",true)
        isValid = false
    }
    if (!cb_terms_and_condition.isChecked){
            showErrorSnackBar("Please agree terms and condition.", true)
            false
    }
    return isValid
    }

    private fun registerUser(){

        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = et_email_id.text.toString().trim { it <= ' ' }
            val password: String = et_password1.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    task ->

                    hideProgressDialog()

                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    val user = User(
                        firebaseUser.uid,
                        et_first_name.text.toString().trim(),
                        et_last_name.text.toString().trim(),
                        et_email_id.text.toString().trim(),
                        )

                    FireStoreClass().registerUser(this@RegisterActivity,user)

                    startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                }
                else {
                    hideProgressDialog()
                    showErrorSnackBar(task.exception!!.message.toString() , true)
                }
            }
        }
    }

    fun userRegisteredSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

    }
}