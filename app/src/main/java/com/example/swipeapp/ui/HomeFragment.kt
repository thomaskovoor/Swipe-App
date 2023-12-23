package com.example.swipeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView;
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swipeapp.R
import com.example.swipeapp.adapter.RecAdapter
import com.example.swipeapp.dataclass.ProductResponse
import com.example.swipeapp.dataclass.Resource
import com.example.swipeapp.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton


class HomeFragment : Fragment() {

    private var progressBar:CustomProgressBar?=null
    lateinit var recyclerAdapter : RecAdapter
    private lateinit var searchView:SearchView
    private lateinit var dataList: List<ProductResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        progressBar = CustomProgressBar(activity)

        searchView = view.findViewById(R.id.search_view)
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                filterData(p0)
                return true
            }

        })



        val recyclerView = view.findViewById<RecyclerView>(R.id.rec_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter= RecAdapter(requireContext())
        recyclerView.adapter = recyclerAdapter

        viewModel.getApiData()

        viewModel.apiLiveData.observe(viewLifecycleOwner){result->
        when(result){
            is Resource.Loading->{
             progressBar!!.showDialog()
            }
            is Resource.Failure->{

                Toast.makeText(activity, result.errorBody,Toast.LENGTH_LONG).show()
            }
            is Resource.Success->{
            progressBar!!.dismissDialog()
                dataList = result.value
                recyclerAdapter.setData(result.value as MutableList<ProductResponse>)
                recyclerAdapter.notifyDataSetChanged()

            }
        }

        }

        val addProductButton:MaterialButton = view.findViewById(R.id.addProductFab)

        addProductButton.setOnClickListener{
        findNavController().navigate(R.id.action_homeFragment_to_addProductFragment)
        }




        return view
    }

    private fun filterData(p0: String?) {
         val filteredList: ArrayList<ProductResponse> = ArrayList()
           for (product:ProductResponse in dataList){
               if(product.product_name.toLowerCase().contains(p0!!.toLowerCase())
                   || product.product_type.toLowerCase().contains(p0.toLowerCase())){
                   filteredList.add(product)
               }
           }
        if(filteredList.isEmpty())
            Toast.makeText(activity,"No data found",Toast.LENGTH_LONG).show()
        else{
            recyclerAdapter.setData(filteredList as MutableList<ProductResponse>)
            recyclerAdapter.notifyDataSetChanged()
        }
    }


}