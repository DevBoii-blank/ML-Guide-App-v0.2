package com.example.mobilelegendsbuildguideapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mobilelegendsbuildguideapp.Common.Common
import com.example.mobilelegendsbuildguideapp.Model.LoadingDialog
import com.example.mobilelegendsbuildguideapp.Model.NoticeDialog
import com.example.mobilelegendsbuildguideapp.Model.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    lateinit var etEmail: TextInputLayout
    lateinit var etPass: TextInputLayout
    lateinit var forgotPass: LinearLayout
    lateinit var btSignIn: Button
    lateinit var btNewAccount: Button

    //FIREBASE
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var users: DatabaseReference

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        toolbar = findViewById(R.id.toolbar)

        etEmail = findViewById(R.id.etEmailAddressLayout)
        etPass = findViewById(R.id.etPasswordLayout)
        forgotPass = findViewById(R.id.ForgotPass)
        btSignIn = findViewById(R.id.btSignIn)
        btNewAccount = findViewById(R.id.btCreateAccount)

        //init firebase
        //auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
        users = db.getReference(Common.user_tbl) //UsersInformation


        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        btSignIn.setOnClickListener {
            authenticateUserLoginIn()
        }

        btNewAccount.setOnClickListener {
            displaySignUpDialog()
            //startActivity(Intent(this, SignUpActivity::class.java)) //use
        }

        forgotPass.setOnClickListener {
            displayForgotDialog()
        }

    }

    private fun displayForgotDialog() {
        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.CustomDialog)

        val msignupView: View = LayoutInflater.from(baseContext).inflate(R.layout.forgot_password, null)
        var mEmail: TextInputLayout = msignupView.findViewById(R.id.etEmailAdLayout)
        val mSend: Button = msignupView.findViewById(R.id.btSignUp)
        //val mExit: ImageView = msignupView.findViewById(R.id.close_button)
        val mExit: LinearLayout = msignupView.findViewById(R.id.close_x_layout)

        mBuilder.setView(msignupView)
        //val dialog: AlertDialog = mBuilder.create()
        val dialog: Dialog = mBuilder.create()
        dialog.show()

        mSend.setOnClickListener {
            val email: String = mEmail.editText?.text.toString().trim()

            if (email.isEmpty()) {
                mEmail.error = "Field can't be empty"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.error = "Please enter a valid email!"
            } else {
                if (!mEmail.error.isNullOrEmpty()) mEmail.error = null

                loadingDialog = LoadingDialog(this)
                loadingDialog.showDialog()

                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        loadingDialog.close()

                        val notice: NoticeDialog = NoticeDialog(this)
                        notice.showDialog(
                            getString(R.string.Notice_body_1),
                            "We've sent you a reset link in your email",
                            "Click the link to change your password then proceed to Sign in")
                        dialog.dismiss()

                    } else {
                        loadingDialog.close()
                        Toast.makeText(baseContext, "Email doesn't exist", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                        loadingDialog.close()
                        Toast.makeText(baseContext, "Password reset failed " + exception.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
        mExit.setOnClickListener { dialog.dismiss() }
    }

    private fun displaySignUpDialog() {
        //Toast.makeText(this@LoginActivity, "Create Account clicked", Toast.LENGTH_SHORT).show()
        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.CustomDialog)

        val msignupView: View = LayoutInflater.from(baseContext).inflate(R.layout.pop_up_signup, null)
        var mUsername: TextInputLayout = msignupView.findViewById(R.id.etUsernameLayout)
        var mEmail: TextInputLayout = msignupView.findViewById(R.id.etEmailAdLayout)
        var mPass: TextInputLayout = msignupView.findViewById(R.id.etPassLayout)
        val mSignUpButton: Button = msignupView.findViewById(R.id.btSignUp)
        //val mExit: ImageView = msignupView.findViewById(R.id.close_button)
        val mExit: LinearLayout = msignupView.findViewById(R.id.close_x_layout)

        mBuilder.setView(msignupView)
        //val dialog: AlertDialog = mBuilder.create()
        val dialog: Dialog = mBuilder.create()
        dialog.show()

        mSignUpButton.setOnClickListener {
            val username: String = mUsername.editText?.text.toString().trim()
            val email: String = mEmail.editText?.text.toString().trim()
            val password: String = mPass.editText?.text.toString().trim()

            val verify_1 = constraintUsername(mUsername)
            val verify_2 = constraintEmail(mEmail)
            val verify_3 = constraintPassword(mPass)

            if (verify_1 && verify_2 && verify_3 ) {
                //show loading progress
                loadingDialog = LoadingDialog(this)
                loadingDialog.showDialog()

                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(this) {
                    val newUser = User(username, email)

                    //Info of the new user is saved to firebase
                    users.child(auth.currentUser!!.uid).setValue(newUser).addOnSuccessListener(this) {
                        dialog.dismiss()

                        auth.currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                loadingDialog.close()

                                //create dialog to verify email
                                //noticeAlertDialog()
                                val notice: NoticeDialog = NoticeDialog(this)
                                notice.showDialog(
                                    getString(R.string.Notice_body_1),
                                    getString(R.string.Notice_body_2),
                                    getString(R.string.Notice_body_3)
                                )

                                //ALTERNATIVE WAY TO REMOVE INTSTANCE OF CURRENTUSER
                                //----------IMPORTANT!----------------
                                auth.signOut()
                            } else {
                                loadingDialog.close()
                                Toast.makeText(baseContext, "Sign Up Failed, " + task.exception!!.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener(this) { exception ->
                        loadingDialog.close()
                        Toast.makeText(baseContext, "Sign Up Failed, " + exception.message, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener(this) { exception ->
                    loadingDialog.close()
                    //Toast.makeText(baseContext, "Sign Up Failed" , Toast.LENGTH_SHORT)
                    Toast.makeText(
                        baseContext,
                        "Sign Up Failed, " + exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else {
                //Toast.makeText(baseContext,  mUsername.editText?.text.toString(), Toast.LENGTH_SHORT).show()

                Toast.makeText(baseContext, "Please enter all the required fields", Toast.LENGTH_SHORT).show()
            }
        }
        mExit.setOnClickListener { dialog.dismiss() }
    }

    private fun constraintUsername(mUsername: TextInputLayout): Boolean {
        val username: String = mUsername.editText?.text.toString().trim()

        if (username.isEmpty()) {
            mUsername.error = "Field can't be empty"
            return false
        } else if (username.length < 5) {
            mUsername.error = "Username must be at least have 5 characters"
            return false
        } else {
            if (!mUsername.error.isNullOrEmpty()) mUsername.error = null
            return true
        }
    }

    private fun constraintEmail(mEmail: TextInputLayout): Boolean {
        val email: String = mEmail.editText?.text.toString().trim()

        if (email.isEmpty()) {
            mEmail.error = "Field can't be empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.error = "Please enter a valid email!"
            return false
        } else {
            if (!mEmail.error.isNullOrEmpty()) mEmail.error = null
            return true
        }
    }

    private fun constraintPassword(mPass: TextInputLayout): Boolean {
        val password: String = mPass.editText?.text.toString().trim()

        if (password.isEmpty()) {
            mPass.error = "Field can't be empty"
            return false
        } else if (Common.SP_CHARS_PATTERN.matcher(password).matches()) {
            mPass.error = "Use at least 6 characters and a mix of higher and lower case letters and numbers with no special character in it"
            return false
        } else if (!Common.PASSWORD_PATTERN.matcher(password).matches()) {
            mPass.error = "Use at least 6 characters and a mix of higher and lower case letters and numbers with no special character in it"
            return false
        } else {
            if (!mPass.error.isNullOrEmpty()) mPass.error = null
            return true
        }
    }

    private fun noticeAlertDialog() {
        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AppThemeDialog)

        val msignupView: View = LayoutInflater.from(baseContext).inflate(R.layout.alert_dialog, null)
        val mOkayButton: Button = msignupView.findViewById(R.id.btOkay)
        val mClose: ImageView = msignupView.findViewById(R.id.x_button)

        mBuilder.setView(msignupView)
        val dialog: AlertDialog = mBuilder.create()
        //val dialog: Dialog = mBuilder.create()
        dialog.show()

        mOkayButton.setOnClickListener { dialog.dismiss() }
        mClose.setOnClickListener { dialog.dismiss() }
    }

    private fun authenticateUserLoginIn() {
        val email: String = etEmail.editText?.text.toString().trim()
        val password: String = etPass.editText?.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                etEmail.error = "Please enter your email address" //change to textinputlayout error
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Please enter a valid email address!"
            } else {
                etEmail.error = null
            }

            if (password.isEmpty()) {
                etPass.error = "Please enter your password"
            } else {
                etPass.error = null
            }

            Toast.makeText(applicationContext, "Please enter all the details", Toast.LENGTH_SHORT).show()
            return
        }

        loadingDialog = LoadingDialog(this)
        loadingDialog.showDialog()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    loadingDialog.close()

                    if (auth.currentUser!!.isEmailVerified) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signInWithEmail:", "success")
                        val user = auth.currentUser
                        val userID = user!!.uid
                        users.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(baseContext, "Error getting Profile Info", Toast.LENGTH_SHORT).show()
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val sharedPreferences = getSharedPreferences(Common.SHARED_PREFS, Context.MODE_PRIVATE)
                                    val editor: SharedPreferences.Editor = sharedPreferences.edit()

                                    editor.putString(Common.USERNAME, snapshot.child("username").value.toString())
                                    editor.putString(Common.EMAIL, snapshot.child("email_address").value.toString())
                                    editor.apply()
                                }
                            })
                        updateUI(user)
                    } else {
                        Toast.makeText(baseContext, "Email verification required. Please check your email for verification.", Toast.LENGTH_SHORT).show()
                        auth.signOut()
                    }
                } else {
                    loadingDialog.close()
                    // If sign in fails, display a message to the user.
                    Log.w("signInWithEmail:", "failure", task.exception)
                    //Toast.makeText(baseContext, "Signing In failed, " + task.exception, Toast.LENGTH_SHORT).show()
                    updateUI(null)
                    // ...
                }
                // ...
            }
    }

    //to sign out a user
    //Firebase.auth.signOut()
    //Common.currentUser.delete()

    //also use this in splashscreen
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        //val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
                val sharedPreferences = getSharedPreferences(Common.SHARED_PREFS, Context.MODE_PRIVATE)
                val intent: Intent = Intent(this, MainActivity::class.java)
                //intent.putExtra("username", sharedPreferences.getString(Common.USERNAME, ""))
                startActivity(intent)
                finish()
        } else {
            Toast.makeText(baseContext, "Sign in Authentication failed.", Toast.LENGTH_SHORT).show()
        }
    }

}