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


/**
 * A Fragment representing the home screen of the application.
 *
 * This Fragment displays a list of products and provides a search functionality to filter the products. It also provides a button to navigate to the AddProductFragment.
 *
 * @property progressBar The CustomProgressBar used to display a loading indicator.
 * @property recyclerAdapter The adapter used for the RecyclerView.
 * @property searchView The SearchView used for filtering the products.
 * @property dataList The list of products.
 */
class HomeFragment : Fragment() {

    private var progressBar:CustomProgressBar?=null
    lateinit var recyclerAdapter : RecAdapter
    private lateinit var searchView:SearchView
    private lateinit var dataList: List<ProductResponse>

    /**
     * This method inflates the fragment_home layout, initializes the RecyclerView and its adapter, sets up the SearchView, and observes the apiLiveData from the ViewModel.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        /**
         * Initialize ViewModel, ProgressBar, RecyclerView, and its adapter
         * Set up SearchView and observe apiLiveData from ViewModel
        */

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

    /**
    * Filters the products based on the given query.
    *
    * This method creates a new list of products that contain the query in their name or type, and updates the RecyclerView with the filtered list.
    *
    * @param p0 The query used to filter the products.
    */
    private fun filterData(p0: String?) {
        /**
         *  Filter the products and update the RecyclerView
          */

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