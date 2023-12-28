package com.example.swipeapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class AddProductFragment : Fragment() {
    var productType = arrayOf("Electronics", "Service", "Grocery", "Beauty", "Toys", "Other")
    private var progressBar: CustomProgressBar? = null
    var selectedImageUri: Uri? = null
    private var filePath: String? = null

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

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
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }




        submitBtn.setOnClickListener {
            if (editTextProductName.text!!.isEmpty() || editTextProductTax.text!!.isEmpty()
                || editTextProductSellingPrice.text!!.isEmpty()
            ) {
                Toast.makeText(context, "One or more fields empty!!", Toast.LENGTH_LONG).show()
            } else {
                if (selectedImageUri != null) {

                    viewModel.addProduct(
                        editTextProductName.text.toString(),
                        productTypeSpinner.selectedItem.toString(),
                        editTextProductSellingPrice.text.toString(),
                        editTextProductTax.text.toString(),
                        path = filePath// Pass the MultipartBody parts
                    )
                } else {

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
                .setBackgroundTint(Color.parseColor("#4A90E2"))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            val newFileName = "my_image"
            filePath = copyFileToInternalStorage(selectedImageUri!!, newFileName, requireContext())
            Toast.makeText(requireActivity(), selectedImageUri.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(uri, proj, null, null, null)
            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            return column_index?.let { cursor!!.getString(it) }
        } finally {
            cursor?.close()
        }
    }
    private fun copyFileToInternalStorage(uri: Uri, newFileName: String, context: Context): String {
        val fileExtension = ".jpg" // Default to .jpg if you can't determine the file type

        val directory = context.filesDir
        val newFile = File(directory, newFileName + fileExtension)

        try {
            copy(context.contentResolver.openInputStream(uri)!!, FileOutputStream(newFile))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return directory.absolutePath + "/" + newFileName + fileExtension
    }

    @Throws(Exception::class)
    private fun copy(src: InputStream, dst: FileOutputStream) {
        try {
            val buf = ByteArray(1024)
            var len: Int
            while (src.read(buf).also { len = it } > 0) {
                dst.write(buf, 0, len)
            }
        } finally {
            src.close()
            dst.close()
        }
    }


}