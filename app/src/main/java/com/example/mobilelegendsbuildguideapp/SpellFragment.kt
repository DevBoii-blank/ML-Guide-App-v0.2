package com.example.mobilelegendsbuildguideapp


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mobilelegendsbuildguideapp.Common.Common
import com.example.mobilelegendsbuildguideapp.Model.Guide
import com.example.mobilelegendsbuildguideapp.Model.LoadingDialog
import com.example.mobilelegendsbuildguideapp.Model.NoticeDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class SpellFragment : Fragment() {

    //FIREBASE
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var users: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val SpellFragment: View = inflater.inflate(R.layout.fragment_spell, container, false)

        //init firebase
        //auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
        users = db.getReference(Common.guide_tbl) //Guides

        //edittext.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)


        return SpellFragment
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {

    }

}