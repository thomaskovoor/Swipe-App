package com.example.swipeapp.ui

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.FragmentActivity

/**
 * A class that provides a custom progress bar dialog.
 *
 * This class uses a Dialog to display a custom progress bar. The progress bar is defined in the dialog_loading layout.
 *
 * @property activity The activity in which the progress bar is to be displayed.
 * @property dialog The Dialog that displays the progress bar.
 */
class CustomProgressBar(private var activity: FragmentActivity?) {
    private var dialog: Dialog? = null

    /**
     * Displays the progress bar dialog.
     *
     * This method creates a new Dialog, sets its content view to the dialog_loading layout, and displays it.
     * The dialog is not cancelable.
     */
    fun  showDialog(){
        dialog = Dialog(activity!!)
        dialog!!.setContentView(com.example.swipeapp.R.layout.dialog_loading)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        dialog!!.show()
        dialog!!.setCancelable(false)
    }

    /**
     * Dismisses the progress bar dialog.
     *
     * This method dismisses the dialog and allows it to be canceled on touch outside.
     */
    fun dismissDialog(){
        if(dialog == null){
            dialog = Dialog(activity!!)
        }
        dialog!!.dismiss()
        dialog!!.setCanceledOnTouchOutside(true)
    }

    /**
     * Returns the visibility of the progress bar dialog.
     *
     * @return A Boolean indicating whether the dialog is currently showing.
     */
    fun getVisibility(): Boolean {
        return dialog!!.isShowing
    }
}