package com.example.mobilelegendsbuildguideapp.Model

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.example.mobilelegendsbuildguideapp.R

class LoadingDialog {
    private val activity: Activity
    private lateinit var dialog: Dialog

    constructor(activity: Activity) {
        this.activity = activity
    }

    public fun showDialog() {
        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.LoadingDialog)
        val mView: View = LayoutInflater.from(activity).inflate(R.layout.loading_dialog, null)
        mBuilder.setView(mView)
        dialog = mBuilder.create()

        //dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        //dialog.setContentView(R.layout.loading_dialog)
        dialog.show()
    }

    public fun close() {
        dialog.dismiss()
    }

}