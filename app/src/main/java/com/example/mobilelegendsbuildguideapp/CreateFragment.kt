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


class CreateFragment : Fragment() {

    lateinit var Create: Button
    lateinit var Hero_text: TextInputLayout
    lateinit var Early_text: TextInputLayout
    lateinit var Mid_text: TextInputLayout
    lateinit var Late_text: TextInputLayout
    lateinit var Save: Button
    lateinit var Cancel: Button
    lateinit var ParentLayout: LinearLayout
    lateinit var SignIn: Button

    private lateinit var loadingDialog: LoadingDialog

    //FIREBASE
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var users: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val CreateFragment: View = inflater.inflate(R.layout.fragment_create, container, false)

        Create = CreateFragment.findViewById(R.id.btCreate_Guide)
        Save = CreateFragment.findViewById(R.id.btSaveGuide)
        Cancel = CreateFragment.findViewById(R.id.btCancelGuide)
        Hero_text = CreateFragment.findViewById(R.id.etHeroNameLayout)
        Early_text = CreateFragment.findViewById(R.id.etEarlyGameLayout)
        Mid_text = CreateFragment.findViewById(R.id.etMidGameLayout)
        Late_text = CreateFragment.findViewById(R.id.etLateGameLayout)
        ParentLayout = CreateFragment.findViewById(R.id.GuideParentLayout)

        SignIn = CreateFragment.findViewById(R.id.btLogin)

        //init firebase
        //auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
        users = db.getReference(Common.guide_tbl) //Guides

        //edittext.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)

        Create.setOnClickListener {
            makeAGuide()
        }


        return CreateFragment
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                SignIn.visibility = Button.GONE

                Create.visibility = Button.VISIBLE
            } else {
                Toast.makeText(activity, "Email verification required. Please check your email for verification.", Toast.LENGTH_SHORT).show()
            }
        } else {
            SignIn.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
    }

    private fun makeAGuide() {
        Create.visibility = Button.INVISIBLE
        ParentLayout.visibility = LinearLayout.VISIBLE

        Cancel.setOnClickListener {
            Create.visibility = Button.VISIBLE
            ParentLayout.visibility = LinearLayout.GONE
        }

        Save.setOnClickListener {
            val HeroName: String = Hero_text.editText?.text.toString().trim()
            val Guide_for_early: String = Early_text.editText?.text.toString().trim()
            val Guide_for_middle: String = Mid_text.editText?.text.toString().trim()
            val Guide_for_late: String = Late_text.editText?.text.toString().trim()

            if(HeroName.isEmpty() || Guide_for_early.isEmpty() || Guide_for_middle.isEmpty() || Guide_for_late.isEmpty()) {
                Toast.makeText(activity, "Please enter all the details", Toast.LENGTH_SHORT).show()
            } else {
                var creator: String? = ""
                activity?.let {
                    loadingDialog = LoadingDialog(it)
                    loadingDialog.showDialog()

                    val sharedPreferences: SharedPreferences = it.getSharedPreferences(Common.SHARED_PREFS, Context.MODE_PRIVATE)
                    creator = sharedPreferences.getString(Common.USERNAME, "")
                }

                val newGuide = Guide(creator,HeroName, Guide_for_early, Guide_for_middle, Guide_for_late)
                users.child(auth.currentUser!!.uid).setValue(newGuide).addOnSuccessListener {
                    loadingDialog.close()

                    Create.visibility = Button.VISIBLE
                    ParentLayout.visibility = LinearLayout.GONE
                }.addOnFailureListener { exception ->
                    loadingDialog.close()
                    Toast.makeText(activity, "Error saving in the database," + exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}