package com.example.swipeapp.network


import com.example.swipeapp.dataclass.AddProductResponse
import com.example.swipeapp.dataclass.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

    @GET("get")
    fun getProductData(): Call<List<ProductResponse>>

    @Multipart
    @POST("add")
    fun addProductData(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: List<MultipartBody.Part>? = null
    ): Call<AddProductResponse>

}