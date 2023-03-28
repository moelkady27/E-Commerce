package com.example.ecommerce.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.firestore.FireStoreClass
import com.example.ecommerce.models.Address
import com.example.ecommerce.models.CartItem
import com.example.ecommerce.models.Order
import com.example.ecommerce.models.Product
import com.example.ecommerce.ui.adapter.CartItemsListAdapter
import com.example.ecommerce.utils.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_checkout.*
import java.util.ArrayList

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: Address ?= null

    private lateinit var mProductList: ArrayList<Product>

    private lateinit var mCartItemsList: ArrayList<CartItem>

    private var mSubTotal: Double = 0.0

    private var mTotalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECT_ADDRESS)!!
        }

        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }

        getProductList()

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }

    private fun setupActionBar(){

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24)
        }

        toolbar_checkout_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun successProductsListFromFireStore(productList: ArrayList<Product>) {
        mProductList = productList
        getCartItemsList()
    }

    private fun getCartItemsList(){
        FireStoreClass().getCartList(this@CheckoutActivity)
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItemsList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemsList){
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }

        tv_checkout_sub_total.text = "$$mSubTotal"
        tv_checkout_shipping_charge.text = "$10.0"

        if (mSubTotal > 0 ) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "$$mTotalAmount"
        }
        else{
            ll_checkout_place_order.visibility = View.GONE
        }
    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getAllProductsList(this@CheckoutActivity)
    }

    private fun placeAnOrder(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mAddressDetails != null){
            val order = Order(
                FireStoreClass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.0",
                mTotalAmount.toString(),
                System.currentTimeMillis()

            )

            FireStoreClass().placeOrder(this , order)
        }
    }

    fun orderPlacedSuccess(){
        FireStoreClass().updateAllDetails(this , mCartItemsList)
    }

    fun allDetailsUpdatedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(
            this@CheckoutActivity,
            "You order was placed successfully",
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(this@CheckoutActivity , DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}