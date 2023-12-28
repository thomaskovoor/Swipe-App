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

/**
 * An interface defining the API endpoints for the application.
 *
 * This interface uses Retrofit annotations to encode details about the HTTP requests.
 */
interface ApiInterface {

    /**
     * Get a list of products from the server.
     *
     * This method makes a GET request to the "get" endpoint and expects a list of ProductResponse objects in the response.
     *
     * @return A Call object that can be used to make the request.
     */
    @GET("get")
    fun getProductData(): Call<List<ProductResponse>>

    /**
     * Add a new product to the server.
     *
     * This method makes a POST request to the "add" endpoint and sends a multipart request body containing the product details.
     *
     * @param productName The name of the product.
     * @param productType The type of the product.
     * @param price The price of the product.
     * @param tax The tax applied to the product.
     * @param files An optional part containing a file to be uploaded.
     *
     * @return A Call object that can be used to make the request.
     */
    @Multipart
    @POST("add")
    fun addProductData(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: MultipartBody.Part?
    ): Call<AddProductResponse>

}