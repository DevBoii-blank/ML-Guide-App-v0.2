package com.example.mobilelegendsbuildguideapp.Model

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.mobilelegendsbuildguideapp.R

class NoticeDialog {
    private val activity: Activity
    private lateinit var dialog: AlertDialog
    private lateinit var textView_1: TextView
    private lateinit var textView_2: TextView
    private lateinit var textView_3: TextView
    lateinit var confirmButton: Button

    constructor(activity: Activity) {
        this.activity = activity
    }

    public fun showDialog(text_1: String, text_2: String, text_3: String) {
        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.AppThemeDialog)
        val mView: View = LayoutInflater.from(activity).inflate(R.layout.alert_dialog, null)
        confirmButton = mView.findViewById(R.id.btOkay)
        //val mClose: ImageView = mView.findViewById(R.id.x_button)
        val mClose: LinearLayout = mView.findViewById(R.id.close_x_layout)
        textView_1 = mView.findViewById(R.id.textview_1)
        textView_2 = mView.findViewById(R.id.textview_2)
        textView_3 = mView.findViewById(R.id.textview_3)
        textView_1.text = text_1
        textView_2.text = text_2
        textView_3.text = text_3

        mBuilder.setView(mView)
        dialog = mBuilder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        //dialog.setContentView(R.layout.loading_dialog)
        dialog.show()

        confirmButton.setOnClickListener { dialog.dismiss() }
        mClose.setOnClickListener { dialog.dismiss() }
    }

    public fun close() {
        dialog.dismiss()
    }

}