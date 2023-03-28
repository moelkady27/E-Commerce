package com.example.ecommerce.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.firestore.FireStoreClass
import com.example.ecommerce.models.CartItem
import com.example.ecommerce.models.Product
import com.example.ecommerce.ui.adapter.CartItemsListAdapter
import com.example.ecommerce.ui.adapter.MyProductsListAdapter
import com.example.ecommerce.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {

    private lateinit var mProductsList : ArrayList<Product>
    private lateinit var mCartListItems : ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        setupActionBar()

        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity , AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS , true)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24)
        }

        toolbar_cart_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun successItemsList(cartList: ArrayList<CartItem>){
        hideProgressDialog()

        for (product in mProductsList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if (cartList.size > 0) {
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems , true)
            rv_cart_items_list.adapter = cartListAdapter

            var subTotal: Double = 0.0
            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }
            tv_sub_total.text = "$${subTotal}"
            tv_shipping_charge.text = "$10.0"

            if (subTotal > 0 ){
                ll_checkout.visibility = View.VISIBLE

                val total = subTotal + 10
                tv_total_amount.text = "$${total}"
            }
            else{
                ll_checkout.visibility = View.GONE
            }
        }
        else{
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    private fun getCartItemList(){
        FireStoreClass().getCartList(this@CartListActivity)
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>){
        hideProgressDialog()
        mProductsList = productsList

        getCartItemList()
    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAllProductsList(this)
    }

    fun itemRemovedSuccess(){
        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_LONG
        ).show()

        getCartItemList()
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemList()
    }
}