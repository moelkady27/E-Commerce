package com.example.ecommerce.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.firestore.FireStoreClass
import com.example.ecommerce.models.User
import com.example.ecommerce.utils.Constants
import kotlinx.android.synthetic.main.activity_register.et_email_id
import kotlinx.android.synthetic.main.activity_register.et_first_name
import kotlinx.android.synthetic.main.activity_register.et_last_name
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.tv_title
import java.io.IOException

class UserProfileActivity : BaseActivity() , View.OnClickListener {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)
        et_email_id.isEnabled = false
        et_email_id.setText(mUserDetails.email)

        if (mUserDetails.profileCompleted == 0){
            tv_title.text = resources.getText(R.string.complete_profile)
            et_first_name.isEnabled = false
            et_last_name.isEnabled = false

        } else {
            setupActionBar()
            tv_title.text = resources.getText(R.string.edit_profile)
            GlideHolder(this@UserProfileActivity).loadUserPicture(mUserDetails.image,iv_user_photo)
            if (mUserDetails.mobile!= 0L){
                et_mobile.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE){
                rb_male.isChecked = true
            }
            else{
                rb_female.isChecked = true
            }

        }

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_save.setOnClickListener(this@UserProfileActivity)

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24)
        }

        toolbar_user_profile_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_save -> {
                    if (validateUserProfileDetails()){

                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri != null) {

                            FireStoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,Constants.USER_PROFILE_IMAGE)
                        }
                        else{
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
     }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                try {
                    mSelectedImageFileUri = data!!.data

                    Glide
                        .with(this@UserProfileActivity)
                        .load(mSelectedImageFileUri) //Uri.parse(mSelectedImageFileUri.toString())
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(iv_user_photo)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@UserProfileActivity,
                        "image selection failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("request cancelled " , "image selection cancelled")
        }
    }

    private fun validateUserProfileDetails() : Boolean{
        return when{
            TextUtils.isEmpty(et_mobile.text.toString().trim()) ->{
                showErrorSnackBar("please enter your mobile number",true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun userProfileUpdateSuccess(){

        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            "Profile updated successfully!",
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity , DashboardActivity::class.java)) //MainActivity
        finish()
    }

    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageURL = imageURL

        updateUserProfileDetails()
    }

    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String,Any>()

        val firstName = et_first_name.text.toString().trim()
        if (firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = et_last_name.text.toString().trim()
        if (lastName != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = et_mobile.text.toString().trim()

        val gender = if (rb_male.isChecked){
            Constants.MALE
        }
        else {
            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FireStoreClass().updateUserProfileData(this,userHashMap)
    }
}