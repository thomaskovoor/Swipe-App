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


class AddProductViewModel:ViewModel(){

    var _apiLiveData = MutableLiveData<Resource<AddProductResponse>>()
    val apiLiveData: MutableLiveData<Resource<AddProductResponse>> get() = _apiLiveData

    fun addProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        path: String?
    ) {

        _apiLiveData.postValue(Resource.Loading)


        var body: MultipartBody.Part? = null

        if (path != null) {

            val file = File(path)
            if (file.exists()) {
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                body = MultipartBody.Part.createFormData("files", file.name, requestFile)
            }

        }


        viewModelScope.launch {

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

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }


}
