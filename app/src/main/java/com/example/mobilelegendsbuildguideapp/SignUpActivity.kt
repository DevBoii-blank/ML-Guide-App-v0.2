package com.example.mobilelegendsbuildguideapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pop_up_signup)

        val v: View = window.decorView
        v.setBackgroundResource(android.R.color.transparent)
    }
}