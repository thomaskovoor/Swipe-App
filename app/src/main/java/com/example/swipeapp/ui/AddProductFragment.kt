package com.example.swipeapp.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.swipeapp.R
import com.example.swipeapp.dataclass.Resource
import com.example.swipeapp.viewmodel.AddProductViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MultipartBody


class AddProductFragment : Fragment() {
    var productType = arrayOf("Electronics", "Service", "Grocery", "Beauty", "Toys", "")
    private var progressBar: CustomProgressBar? = null
    var selectedImageUri: Uri? = null

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        var productName: TextInputLayout = view.findViewById(R.id.tilProductName)
        var productSellingPrice: TextInputLayout = view.findViewById(R.id.tilSellingPrice)
        var productTax: TextInputLayout = view.findViewById(R.id.tilTaxRate)
        val productTypeSpinner: Spinner = view.findViewById(R.id.productTypeSp)
        val uploadImage: Button = view.findViewById(R.id.uploadImageBtn)
        val submitBtn: Button = view.findViewById(R.id.submitBtn)

        val editTextProductName: TextInputEditText = view.findViewById(R.id.etProductName)
        val editTextProductSellingPrice: TextInputEditText = view.findViewById(R.id.etSellingPrice)
        val editTextProductTax: TextInputEditText = view.findViewById(R.id.etTaxRate)

        val viewModel = ViewModelProvider(this)[AddProductViewModel::class.java]
        progressBar = CustomProgressBar(activity)


        val arrayAdapter = activity?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item,
                productType
            )
        }
        arrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        productTypeSpinner.adapter = arrayAdapter

        productTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Toast.makeText(context,productType[p2],Toast.LENGTH_LONG).show()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        uploadImage.setOnClickListener {
            pickImageFromGallery()
        }




        submitBtn.setOnClickListener {
            if (editTextProductName.text!!.isEmpty() || editTextProductTax.text!!.isEmpty()
                || editTextProductSellingPrice.text!!.isEmpty()
            ) {
                Toast.makeText(context, "One or more fields empty!!", Toast.LENGTH_LONG).show()
            } else {
                if (selectedImageUri != null) {
                    MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                        addFormDataPart("product_name", editTextProductName.text.toString())
                        addFormDataPart("product_type", productTypeSpinner.selectedItem.toString())
                        addFormDataPart("price", editTextProductSellingPrice.text.toString())
                        addFormDataPart("tax", editTextProductTax.text.toString())


                    }.build()


                    viewModel.addProduct(
                        editTextProductName.text.toString(),
                        productTypeSpinner.selectedItem.toString(),
                        editTextProductSellingPrice.text.toString(),
                        editTextProductTax.text.toString(),
                        selectedImageUri// Pass the MultipartBody parts
                    )
                } else {
                    MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                        addFormDataPart("product_name", editTextProductName.text.toString())
                        addFormDataPart("product_type", productTypeSpinner.selectedItem.toString())
                        addFormDataPart("price", editTextProductSellingPrice.text.toString())
                        addFormDataPart("tax", editTextProductTax.text.toString())


                    }.build()


                    viewModel.addProduct(
                        editTextProductName.text.toString(),
                        productTypeSpinner.selectedItem.toString(),
                        editTextProductSellingPrice.text.toString(),
                        editTextProductTax.text.toString(),
                        null
                    )// Pass the MultipartBody parts

                }
            }
        }

        viewModel.apiLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    progressBar!!.showDialog()
                }

                is Resource.Failure -> {
                    progressBar!!.dismissDialog()
                    Toast.makeText(activity, result.errorBody, Toast.LENGTH_LONG).show()
                }

                is Resource.Success -> {
                    progressBar!!.dismissDialog()
                    showSnackBar()
                }
            }
        }
        return view
    }

    private fun showSnackBar() {
        view?.let { rootView ->
            val snackBar = Snackbar.make(rootView, "Data added Successfully", Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(Color.parseColor("#aaf255"))
                .setTextColor(Color.parseColor("#000000"))
                .setAction("OK") {
                    // Dismiss
                }
            snackBar.setActionTextColor(Color.parseColor("#000000"))
            val snackBarView = snackBar.view
            // Get the TextView from the SnackBar view
            val textView =
                snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            // Set the text size (change the value as needed)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            // Show the SnackBar
            snackBar.show()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data

            Toast.makeText(requireActivity(), selectedImageUri.toString(), Toast.LENGTH_LONG).show()
        }
    }


}