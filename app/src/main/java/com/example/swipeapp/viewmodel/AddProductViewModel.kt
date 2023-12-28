package com.example.swipeapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipeapp.dataclass.AddProductResponse
import com.example.swipeapp.dataclass.Resource
import com.example.swipeapp.network.ApiInterface
import com.example.swipeapp.network.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


/**
 * A ViewModel class for managing UI-related data in the lifecycle of the add product screen of the application.
 *
 * This ViewModel provides a LiveData for observing the response from the API when adding a product and a method for adding a product.
 *
 * @property _apiLiveData The MutableLiveData that holds the Resource of the add product response. This is private and mutable.
 * @property apiLiveData The LiveData for observing the add product response. This is public and immutable.
 */
class AddProductViewModel:ViewModel(){
    /**
     * The MutableLiveData that holds the Resource of the add product response. This is private and mutable.
     */
    var _apiLiveData = MutableLiveData<Resource<AddProductResponse>>()
    val apiLiveData: MutableLiveData<Resource<AddProductResponse>> get() = _apiLiveData

    /**
     * Adds a product to the API.
     *
     * This method uses Retrofit to make a network request to the API. It posts the Resource to _apiLiveData based on the result of the request.
     *
     * @param productName The name of the product.
     * @param productType The type of the product.
     * @param price The price of the product.
     * @param tax The tax rate of the product.
     * @param path The path of the product image file.
     */
    fun addProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        path: String?
    ) {
        /**
         *  Post Loading Resource to _apiLiveData
         * Create MultipartBody.Part for the image file if it exists
         * Make network request using Retrofit
         * Post Success or Failure Resource to _apiLiveData based on the result of the request
         */

        _apiLiveData.postValue(Resource.Loading)

/**
 * Create MultipartBody.Part for the image file if it exists
 */
        var body: MultipartBody.Part? = null

        if (path != null) {

            val file = File(path)
            if (file.exists()) {
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                body = MultipartBody.Part.createFormData("files", file.name, requestFile)
            }

        }


        viewModelScope.launch {
        /**
         * Make network request using Retrofit
         * Post Success or Failure Resource to _apiLiveData based on the result of the request
         * @param call The call to the API.
         * @param response The response from the API
             */
            val apiInterface =
                RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
            apiInterface.addProductData(
                toRequestBody(productName),
                toRequestBody(productType),
                toRequestBody(price),
                toRequestBody(tax),
                body
            )
                .enqueue(
                    object : Callback<AddProductResponse> {
                        override fun onResponse(
                            call: Call<AddProductResponse>,
                            response: Response<AddProductResponse>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body()!!.message == "Product added Successfully!") {
                                    _apiLiveData.postValue(Resource.Success(response.body()!!))

                                } else
                                    _apiLiveData.postValue(
                                        Resource.Failure(
                                            false,
                                            0,
                                            response.body()!!.message
                                        )
                                    )
                            }
                        }

                        override fun onFailure(call: Call<AddProductResponse>, t: Throwable) {
                            _apiLiveData.postValue(Resource.Failure(true, 0, "Network Error"))
                            Log.d("error",t.toString())

                        }

                    }
                )
        }

    }

    /**
     * Converts a String to a RequestBody.
     *
     * This method is used to convert a String to a RequestBody, which can be used in a Retrofit request.
     *
     * @param value The String to convert.
     *
     * @return The RequestBody.
     */
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }


}
