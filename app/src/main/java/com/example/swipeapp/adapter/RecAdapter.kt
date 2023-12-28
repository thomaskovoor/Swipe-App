package com.example.swipeapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swipeapp.R
import com.example.swipeapp.dataclass.ProductResponse
import com.squareup.picasso.Picasso



/**
 * RecAdapter is a RecyclerView.Adapter implementation for displaying a list of ProductResponse objects.
 *
 * @property context The context in which the adapter is being used.
 * @property productList The list of ProductResponse objects to display in the RecyclerView.
 */
class RecAdapter( private val context: Context) : RecyclerView.Adapter<RecAdapter.ViewHolder>() {

    private var productList:MutableList<ProductResponse>?=null

    /**
     * Updates the data in the adapter.
     *
     * @param products The new list of ProductResponse objects to display.
     */
    fun setData(products : MutableList<ProductResponse>){
        this.productList = products
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_product,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecAdapter.ViewHolder, position: Int) {
        val product = productList!![position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
           return productList?.size ?: 0
    }

    /**
     * ViewHolder class for RecAdapter.
     *
     * @property view The root view of the ViewHolder.
     * @property productName The TextView displaying the product's name.
     * @property productPrice The TextView displaying the product's price.
     * @property productTax The TextView displaying the product's tax.
     * @property productType The TextView displaying the product's type.
     * @property productImage The ImageView displaying the product's image.
     */
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        private var productName = itemView.findViewById<TextView>(R.id.product_name)
        private var productPrice = itemView.findViewById<TextView>(R.id.product_price)
        private var productTax = itemView.findViewById<TextView>(R.id.tax)
        private var productType = itemView.findViewById<TextView>(R.id.product_type)
        private var productImage:ImageView = itemView.findViewById(R.id.product_image)

        /**
         * Binds a ProductResponse object to the ViewHolder.
         *
         * @param product The ProductResponse object to bind.
         */
        fun bind(product : ProductResponse){
            /** Implementation...
            If there is an image, load it into the ImageView using Picasso.
             else, set the ImageView to a default image.
             */
            productName.text = product.product_name
            val text1 = "₹ "+product.price.toString()
            productPrice.text = text1
            val text2="Tax: "+product.tax.toString()
            productTax.text = text2
            productType.text = product.product_type

            if(product.image.isNotEmpty()){
                Picasso.get().load(product.image).resize(50,50).noFade().into(productImage)
            }
            else
            {
                productImage.setImageResource(R.drawable.resource_package)
            }
        }
    }
    }
