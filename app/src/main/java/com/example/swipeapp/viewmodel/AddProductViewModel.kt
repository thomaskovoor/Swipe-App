package com.example.swipeapp.viewmodel

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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


class AddProductViewModel(application: Application) : AndroidViewModel(application) {

    var _apiLiveData = MutableLiveData<Resource<AddProductResponse>>()
    val apiLiveData: MutableLiveData<Resource<AddProductResponse>> get() = _apiLiveData

    fun addProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageUri: Uri? = null
    ) {

        _apiLiveData.postValue(Resource.Loading)


        val realPath = imageUri?.let { getRealPathFromURI(it) }
        val file = realPath?.let { File(it) }

        val profileImage = file?.let {
            MultipartBody.Part.createFormData(
                name = "files",
                filename = it.name,
                body = it.asRequestBody()
            )
        }


        viewModelScope.launch {

            val apiInterface =
                RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
            apiInterface.addProductData(
                toRequestBody(productName),
                toRequestBody(productType),
                toRequestBody(price),
                toRequestBody(tax),
                listOfNotNull(profileImage)
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

    private val context: Context
        get() = getApplication<Application>()

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }


    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = context.contentResolver.query(contentURI, null, null, null, null)              //.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

}
