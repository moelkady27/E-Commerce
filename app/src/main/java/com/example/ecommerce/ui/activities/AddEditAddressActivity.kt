package com.example.ecommerce.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.ecommerce.R
import com.example.ecommerce.firestore.FireStoreClass
import com.example.ecommerce.models.Address
import com.example.ecommerce.utils.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address.*

class AddEditAddressActivity : BaseActivity() {

    private var mAddressDetails: Address ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {

                tv_title.text = resources.getString(R.string.title_edit_address)
                btn_submit_address.text = resources.getString(R.string.btn_lbl_update)

                et_full_name.setText(mAddressDetails?.name)
                et_phone_number.setText(mAddressDetails?.mobileNumber)
                et_address.setText(mAddressDetails?.address)
                et_zip_code.setText(mAddressDetails?.zipCode)
                et_additional_note.setText(mAddressDetails?.additionalNote)

                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        rb_home.isChecked = true
                    }
                    Constants.OFFICE -> {
                        rb_office.isChecked = true
                    }
                }
            }
        }

        btn_submit_address.setOnClickListener {
            saveAddressToFireStore()
        }

    }

    private fun setupActionBar(){

        setSupportActionBar(toolbar_add_edit_address_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24)
        }

        toolbar_add_edit_address_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun validateData(): Boolean{
        return when{
            TextUtils.isEmpty(et_full_name.text.toString().trim()) -> {
                showErrorSnackBar(
                    "Please enter full name.",
                    true
                )
                false
            }
            TextUtils.isEmpty(et_phone_number.text.toString().trim()) -> {
                showErrorSnackBar(
                    "Please enter phone number.",
                    true
                )
                false
            }
            TextUtils.isEmpty(et_address.text.toString().trim()) -> {
                showErrorSnackBar(
                    "Please enter address.",
                    true
                )
                false
            }
            TextUtils.isEmpty(et_zip_code.text.toString().trim()) -> {
                showErrorSnackBar(
                    "Please enter zip-code.",
                    true
                )
                false
            }

            else -> {
                true
            }
        }
    }

    private fun saveAddressToFireStore(){

        val fullName: String = et_full_name.text.toString().trim()
        val phoneNumber: String = et_phone_number.text.toString().trim()
        val address: String = et_address.text.toString().trim()
        val zipCode: String = et_zip_code.text.toString().trim()
        val additionalNote: String = et_additional_note.text.toString().trim()
        val otherDetails: String = et_other_details.text.toString().trim()

        if (validateData()){
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when{
                rb_home.isChecked -> {
                    Constants.HOME
                }
                else -> {
                    Constants.OFFICE
                }
            }

            val addressModel = Address(
                FireStoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FireStoreClass().updateAddress(this@AddEditAddressActivity, addressModel, mAddressDetails!!.id)
            } else {
                FireStoreClass().addAddress(this@AddEditAddressActivity, addressModel)
            }
        }
    }


    fun addUpdateAddressSuccess(){
        hideProgressDialog()

        val notifySuccessMessage: String = if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        } else {
            resources.getString(R.string.err_your_address_added_successfully)
        }

        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()

        setResult(RESULT_OK)
        finish()
    }
}