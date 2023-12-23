package com.example.swipeapp.dataclass

data class AddProductResponse(
    val message: String,
    val product_details: ProductResponse,
    val product_id: Int,
    val success: Boolean
)

