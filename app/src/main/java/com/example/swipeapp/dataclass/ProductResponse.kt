package com.example.swipeapp.dataclass


/**
 * A data class representing a product response from an API or other data source.
 *
 * This class is typically used to parse a JSON response from an API into an object that can be used in your application.
 *
 * @property image The URL of the product's image.
 * @property price The price of the product.
 * @property product_name The name of the product.
 * @property product_type The type of the product.
 * @property tax The tax applied to the product.
 */
class ProductResponse (
    val image: String,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double
)