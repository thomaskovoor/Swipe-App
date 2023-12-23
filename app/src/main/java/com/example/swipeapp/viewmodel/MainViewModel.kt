package com.example.swipeapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipeapp.dataclass.ProductResponse
import com.example.swipeapp.dataclass.Resource
import com.example.swipeapp.network.ApiInterface
import com.example.swipeapp.network.RetrofitInstance
import com.itkacher.okprofiler.BuildConfig
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainViewModel:ViewModel() {
    var _apiLiveData = MutableLiveData<Resource<List<ProductResponse>>>()
    val apiLiveData:MutableLiveData<Resource<List<ProductResponse>>> get() = _apiLiveData



    fun getApiData(){
        _apiLiveData.postValue(Resource.Loading)

        viewModelScope.launch {

            val apiInterface =RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
            val call = apiInterface.getProductData()
            call.enqueue(object :Callback<List<ProductResponse>?>{
                override fun onResponse(
                    call: Call<List<ProductResponse>?>,
                    response: Response<List<ProductResponse>?>
                ) {
                    if(response.isSuccessful){
                        if(response.body()!!.isNotEmpty()){
                            _apiLiveData.postValue(Resource.Success(response.body()!!))
                        }
                    }
                }

                override fun onFailure(call: Call<List<ProductResponse>?>, t: Throwable) {
                    _apiLiveData.postValue(Resource.Failure(true,0,"Network Error"))
                 //   Toast.makeText(MainApplication.appContext,t.localizedMessage,Toast.LENGTH_SHORT).show()
                }

            })
        }

    }
}