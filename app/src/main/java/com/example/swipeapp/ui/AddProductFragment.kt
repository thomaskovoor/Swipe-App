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


/**
 * A Fragment representing the add product screen of the application.
 *
 * This Fragment provides a form to add a new product. It includes fields for the product's name, type, selling price, and tax rate. It also provides an option to upload an image for the product.
 *
 * @property productType The array of product types.
 * @property progressBar The CustomProgressBar used to display a loading indicator.
 * @property selectedImageUri The Uri of the selected image.
 * @property filePath The path of the selected image file.
 */
class AddProductFragment : Fragment() {

    /**
     * The array of product types.
     */
    var productType = arrayOf("Electronics", "Service", "Grocery", "Beauty", "Toys", "Other")
    private var progressBar: CustomProgressBar? = null
    var selectedImageUri: Uri? = null
    private var filePath: String? = null

    /**
     * The constant PICK_IMAGE_REQUEST.
     */
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * This method inflates the fragment_add_product layout, initializes the form fields, sets up the product type Spinner and the image upload button, and observes the apiLiveData from the ViewModel.
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
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        /**
         * Initialize form fields, Spinner, and buttons
         * Set up image upload button and observe apiLiveData from ViewModel
        */

        val productTypeSpinner: Spinner = view.findViewById(R.id.productTypeSp)
        val uploadImage: Button = view.findViewById(R.id.uploadImageBtn)
        val submitBtn: Button = view.findViewById(R.id.submitBtn)

        val editTextProductName: TextInputEditText = view.findViewById(R.id.etProductName)
        val editTextProductSellingPrice: TextInputEditText = view.findViewById(R.id.etSellingPrice)
        val editTextProductTax: TextInputEditText = view.findViewById(R.id.etTaxRate)

        val viewModel = ViewModelProvider(this)[AddProductViewModel::class.java]
        progressBar = CustomProgressBar(activity)

       /**
         * Set up product type Spinner
        */
        val arrayAdapter = activity?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item,
                productType
            )
        }
        arrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        productTypeSpinner.adapter = arrayAdapter
        /**
         *  Select an item from the product type Spinner
         */
        productTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        /**
         *  Set up image upload button
         *  open gallery to select image
         */

        uploadImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }


        /**
         *  Set up submit button
         *  validate form fields
         *  call addProduct() method from ViewModel
         */

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

        /**
         * Observe apiLiveData from ViewModel
         */
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
    /**
     * Displays a Snack bar with a success message when a product is added successfully.
     */
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
    /**
     * Handles the result of the image selection activity.
     *
     * This method is called when an image is selected from the device's storage. It sets the selectedImageUri and filePath properties based on the selected image.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /**
         *  Handle image selection result
          */

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            val newFileName = "my_image"
            filePath = copyFileToInternalStorage(selectedImageUri!!, newFileName, requireContext())

        }
    }

    /**
     * Copies the selected image file to the app's internal storage.
     *
     * This method is used to create a copy of the selected image file in the app's internal storage, so that it can be used later.
     *
     * @param uri The Uri of the selected image.
     * @param newFileName The name of the new file.
     * @param context The context.
     *
     * @return The path of the new file.
     */
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

    /**
     * Copies data from an InputStream to a FileOutputStream.
     *
     * This method is used to copy data from the InputStream (which is connected to the original image file) to the FileOutputStream (which is connected to the new file in the app's internal storage).
     *
     * @param src The InputStream connected to the original image file.
     * @param dst The FileOutputStream connected to the new file.
     */
    @Throws(Exception::class)
    private fun copy(src: InputStream, dst: FileOutputStream) {
        try {
            /**
             *  Copy the bits from instream to outstream
              */

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