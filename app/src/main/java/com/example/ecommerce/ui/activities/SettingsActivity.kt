package com.example.ecommerce.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.ecommerce.R
import com.example.ecommerce.firestore.FireStoreClass
import com.example.ecommerce.models.User
import com.example.ecommerce.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() , View.OnClickListener{

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupActionBar()

        btn_logout.setOnClickListener(this)
        tv_edit.setOnClickListener(this)
        ll_address.setOnClickListener(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_setting)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24)
        }

        toolbar_setting.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getUserDetails(){

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User){

        mUserDetails = user

        hideProgressDialog()

        GlideHolder(this@SettingsActivity).loadUserPicture(user.image,iv_user_image)

        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_phone.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity,UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS , mUserDetails)
                    startActivity(intent)
                }
                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity,AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}