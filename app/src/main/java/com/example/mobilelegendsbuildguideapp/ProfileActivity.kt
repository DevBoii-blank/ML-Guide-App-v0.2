package com.example.mobilelegendsbuildguideapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mobilelegendsbuildguideapp.Common.Common
import com.example.mobilelegendsbuildguideapp.Model.LoadingDialog
import com.example.mobilelegendsbuildguideapp.Model.NoticeDialog
import com.example.mobilelegendsbuildguideapp.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    private lateinit var myUsername: EditText
    private lateinit var myEmail: EditText
    private  lateinit var  editButton: RelativeLayout
    private  lateinit var  saveButton: RelativeLayout
    private  lateinit var  cancelButton: RelativeLayout
    //private  lateinit var  DeleteAccount: LinearLayout
    //private  lateinit var  SignOut: LinearLayout
    private  lateinit var  iconProfile: ImageView

    //profile update
    private  lateinit var  DeleteAccount: RelativeLayout
    private  lateinit var  SignOut: RelativeLayout

    //FIREBASE
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var users: DatabaseReference

    private lateinit var loadingDialog: LoadingDialog

    private var count: Int = 0

    private var currentUser: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_profile)
        setContentView(R.layout.activity_profile_update)

        //init firebase
        //auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
        users = db.getReference(Common.user_tbl) //UsersInformation

        myUsername = findViewById(R.id.etUsername)
        myEmail = findViewById(R.id.etEmailAddress)
        editButton = findViewById(R.id.btEditProfile)
        saveButton = findViewById(R.id.btSaveProfile)
        cancelButton = findViewById(R.id.btCancelEdit)
        //DeleteAccount = findViewById(R.id.btDelete)
        //SignOut = findViewById(R.id.btSign_Out)
        DeleteAccount = findViewById(R.id.DeleteTab)
        SignOut = findViewById(R.id.SignOutTab)
        //iconProfile = findViewById(R.id.icon)
        toolbar = findViewById(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        editButton.setOnClickListener {
            editTheProfileAction()
        }

        DeleteAccount.setOnClickListener {
            deleteAccountAction()
        }

        SignOut.setOnClickListener {
            signOutUserAction()
        }

        myUsername.isEnabled = false
        myUsername.isFocusableInTouchMode = false
        myUsername.inputType = InputType.TYPE_NULL
        myUsername.setBackgroundResource(R.color.colorPrimaryDark)
        myEmail.isEnabled = false
        myEmail.isFocusableInTouchMode = false
        myEmail.inputType = InputType.TYPE_NULL
        myEmail.setBackgroundResource(R.color.colorPrimaryDark)

        loadData()
    }

    private fun loadData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(Common.SHARED_PREFS, Context.MODE_PRIVATE)
        currentUser.username = sharedPreferences.getString(Common.USERNAME, "")
        currentUser.email_address = sharedPreferences.getString(Common.EMAIL, "")
    }

    private fun editTheProfileAction() {
        //change editText to editable and change background
        myUsername.isEnabled = true
        myUsername.isFocusableInTouchMode = true
        myUsername.setSelection(myUsername.text.length)
        myUsername.inputType = InputType.TYPE_CLASS_TEXT
        //show keyboard
        val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(myUsername.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0)
        myUsername.requestFocus()
        myUsername.setBackgroundResource(R.drawable.textbox_background)

        cancelButton.visibility = RelativeLayout.VISIBLE
        cancelButton.setOnClickListener {
            myUsername.error = null
            myUsername.isEnabled = false
            myUsername.isFocusableInTouchMode = false
            myUsername.inputType = InputType.TYPE_NULL
            myUsername.setBackgroundResource(R.color.colorPrimaryDark)

            saveButton.visibility = RelativeLayout.GONE
            editButton.visibility = RelativeLayout.VISIBLE
            cancelButton.visibility = RelativeLayout.INVISIBLE

            myUsername?.setText(currentUser.username)

        }

        //change icon to save and change cancel button to be visible
        //iconProfile.setImageResource(R.drawable.ic_save)
        saveButton.visibility = RelativeLayout.VISIBLE
        editButton.visibility = RelativeLayout.GONE

        saveButton.setOnClickListener {
            val username: String = myUsername.text.toString().trim()
            if (username.isEmpty()) {
                myUsername.error = "Field can't be empty."
            } else if (username.length < 5) {
                myUsername.error = "Username must be at least have 5 characters"
            } else if (username == currentUser.username) {
                myUsername.error = null
                Toast.makeText(baseContext, "Nope", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(baseContext, "Got in!", Toast.LENGTH_SHORT).show()

                saveButton.visibility = RelativeLayout.GONE
                editButton.visibility = RelativeLayout.VISIBLE
                cancelButton.visibility = RelativeLayout.INVISIBLE

                myUsername.error = null
                myUsername.isEnabled = false
                myUsername.isFocusableInTouchMode = false
                myUsername.inputType = InputType.TYPE_NULL
                myUsername.setBackgroundResource(R.color.colorPrimaryDark)

                loadingDialog = LoadingDialog(this)
                loadingDialog.showDialog()

                val uid = auth.currentUser!!.uid
                users.child(uid).child("username").setValue(username).addOnCompleteListener {
                    loadingDialog.close()

                    val sharedPreferences: SharedPreferences = getSharedPreferences(Common.SHARED_PREFS, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Common.USERNAME, username)
                    editor.apply()

                    Toast.makeText(baseContext, "Username was successfully changed", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { exception ->
                    loadingDialog.close()
                    Toast.makeText(baseContext, "Username change failed, " + exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signOutUserAction() {
        if (auth.currentUser != null) {
            loadingDialog = LoadingDialog(this)
            loadingDialog.showDialog()
            auth.signOut()

            //clear shared preferences on sign out
            val sharedPreferences: SharedPreferences = getSharedPreferences(Common.SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            loadingDialog.close()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun deleteAccountAction() {
        //show dialog
        val notice: NoticeDialog = NoticeDialog(this)
        notice.showDialog(
            "Are you sure?",
            "Once account has been deleted,",
            "All data and information contained will be lost.")
        notice.confirmButton.text = "Confirm"
        notice.confirmButton.setOnClickListener {
            val user = auth.currentUser
            val userID = user?.uid

            notice.close()
            users!!.child(userID!!).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //while this delete all user's data and information stored
                    auth.currentUser!!.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val notice: NoticeDialog = NoticeDialog(this)
                            notice.showDialog(
                                "Deleted!",
                                "Your account has been",
                                "Successfully Deleted.")
                            notice.confirmButton.setOnClickListener {
                                notice.close()

                                val sharedPreferences: SharedPreferences = getSharedPreferences(Common.SHARED_PREFS, Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.clear()
                                editor.apply()

                                startActivity(Intent(baseContext, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            Toast.makeText(baseContext, "Account Deletion failed, " + task.exception!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(baseContext, "Account Deletion failed, " + task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(baseContext, "Account Deletion failed, " + exception.message, Toast.LENGTH_SHORT).show()
            }
        }

        //this deletes only the email in authentication but not the data
        /*auth.currentUser!!.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //while this delete all user's data and information stored

                        val notice: NoticeDialog = NoticeDialog(this)
                        notice.showDialog(
                            "Deleted!",
                            "Your account has been",
                            "Successfully Deleted.")
                        notice.confirmButton.setOnClickListener {
                            Common.currentUser = User()
                            startActivity(Intent(baseContext, MainActivity::class.java))
                            finish()
                        }
                    }
            }.addOnFailureListener { exception ->
                Toast.makeText(baseContext, "Account Deletion failed, " + exception.message, Toast.LENGTH_SHORT).show()
            }*/
    }

    override fun onStart() {
        super.onStart()

        val signedUser = auth.currentUser
        if (signedUser != null) {
            myUsername?.setText(currentUser.username)

            myEmail?.setText(currentUser.email_address)     //Common.currentUser.email_address
        }
    }

}