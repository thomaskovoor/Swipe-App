package com.example.swipeapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipeapp.dataclass.ProductResponse
import com.example.swipeapp.dataclass.Resource
import com.example.swipeapp.network.ApiInterface
import com.example.swipeapp.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A ViewModel class for managing UI-related data in the lifecycle of the main screen of the application.
 *
 * This ViewModel provides a LiveData for observing the product data from the API and a method for fetching the data.
 *
 * @property _apiLiveData The MutableLiveData that holds the Resource of the product data. This is private and mutable.
 * @property apiLiveData The LiveData for observing the product data. This is public and immutable.
 */
class MainViewModel:ViewModel() {
    var _apiLiveData = MutableLiveData<Resource<List<ProductResponse>>>()
    val apiLiveData:MutableLiveData<Resource<List<ProductResponse>>> get() = _apiLiveData


    /**
     * Fetches the product data from the API.
     *
     * This method uses Retrofit to make a network request to the API. It posts the Resource to _apiLiveData based on the result of the request.
     */
    fun getApiData(){
        _apiLiveData.postValue(Resource.Loading)

        /** Post Loading Resource to _apiLiveData
        * Make network request using Retrofit
        * Post Success or Failure Resource to _apiLiveData based on the result of the request
        */


        viewModelScope.launch {

            val apiInterface =RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
            val call = apiInterface.getProductData()
            call.enqueue(object :Callback<List<ProductResponse>?>{
                override fun onResponse(
                    call: Call<List<ProductResponse>?>,
                    response: Response<List<ProductResponse>?>
                ) {
                    /** Post Success Resource to _apiLiveData if the response is successful and the body is not empty
                     * Post Failure Resource to _apiLiveData if the response is successful and the body is empty
                     * Post Failure Resource to _apiLiveData if the response is not successful
                     */
                    if(response.isSuccessful){
                        if(response.body()!!.isNotEmpty()){
                            _apiLiveData.postValue(Resource.Success(response.body()!!))
                        }
                    }
                }

                override fun onFailure(call: Call<List<ProductResponse>?>, t: Throwable) {
                    _apiLiveData.postValue(Resource.Failure(true,0,"Network Error"))                 //   Toast.makeText(MainApplication.appContext,t.localizedMessage,Toast.LENGTH_SHORT).show()
                }

            })
        }

    }
}