package com.example.swipeapp.dataclass

/**
 * A data class representing the response from an API after adding a product.
 *
 * This class is typically used to parse a JSON response from an API into an object that can be used in your application.
 *
 * @property message A message from the server, typically indicating the success or failure of the operation.
 * @property product_details A `ProductResponse` object containing the details of the product that was added.
 * @property product_id The unique identifier of the product that was added.
 * @property success A Boolean indicating whether the operation was successful.
 */
data class AddProductResponse(
    val message: String,
    val product_details: ProductResponse,
    val product_id: Int,
    val success: Boolean
)

