package com.example.swipeapp.ui

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.FragmentActivity

class CustomProgressBar(private var activity: FragmentActivity?) {
    private var dialog: Dialog? = null

    fun  showDialog(){
        dialog = Dialog(activity!!)
        dialog!!.setContentView(com.example.swipeapp.R.layout.dialog_loading)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        dialog!!.show()
        dialog!!.setCancelable(false)
    }

    fun dismissDialog(){
        if(dialog == null){
            dialog = Dialog(activity!!)
        }
        dialog!!.dismiss()
        dialog!!.setCanceledOnTouchOutside(true)
    }

    fun getVisibility(): Boolean {
        return dialog!!.isShowing
    }
}