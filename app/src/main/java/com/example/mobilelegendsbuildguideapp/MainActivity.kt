package com.example.mobilelegendsbuildguideapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mobilelegendsbuildguideapp.Common.Common
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView

    lateinit var Profile: ImageView
    lateinit var Username: TextView
    lateinit var Reputation: TextView

    lateinit var Sign_in: Button

    //FIREBASE
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var users: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //init firebase
        //auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
        users = db.getReference(Common.user_tbl) //UsersInformation

        drawer = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val navHeader: View = navView.getHeaderView(0)
        Profile = navHeader.findViewById(R.id.profilePic)
        Username = navHeader.findViewById(R.id.tvUsername)
        Reputation = navHeader.findViewById(R.id.tvReputationPts)
        Sign_in = navHeader.findViewById(R.id.SignIn)

        //shared preferences for storing and accessing data temp
        val sharedPreferences = getSharedPreferences(Common.SHARED_PREFS, Context.MODE_PRIVATE)
        val toggle:ActionBarDrawerToggle = object:ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                Username.text = sharedPreferences.getString(Common.USERNAME, "")
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)

                Username.text = sharedPreferences.getString(Common.USERNAME, "")
            }
        }


        //toolbar.setNavigationIcon(R.drawable.ic_menu_arrow)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            navView.setCheckedItem(R.id.nav_Home)
        }

    }

    public override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                Sign_in.visibility = Button.GONE

                Profile.visibility = ImageView.VISIBLE
                Username.visibility = TextView.VISIBLE
                Reputation.visibility = TextView.VISIBLE

                val sharedPreferences = getSharedPreferences(Common.SHARED_PREFS, Context.MODE_PRIVATE)
                //Username.text = sharedPreferences.getString(Common.USERNAME, "")
                /*val username: String?
                if(intent != null) {
                    username = intent.getStringExtra("username")
                    Username.text = username
                    //Toast.makeText(baseContext, Username.text, Toast.LENGTH_SHORT).show()
                }*/
                Username.text = sharedPreferences.getString(Common.USERNAME, "")
                //Toast.makeText(baseContext, "Username: " + savedInstances.currentUser.username, Toast.LENGTH_SHORT).show()

                Profile.setOnClickListener {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
            } else {
                Toast.makeText(baseContext, "Email verification required. Please check your email for verification.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Profile.visibility = ImageView.GONE
            Username.visibility = TextView.GONE
            Reputation.visibility = TextView.GONE

            Sign_in.visibility = Button.VISIBLE

            Sign_in.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_Home -> {
                //Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    HomeFragment()
                ).commit()
            }
            R.id.nav_Create -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    CreateFragment()
                ).commit()
            }
            R.id.nav_Emblems -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    EmblemFragment()
                ).commit()
            }
            R.id.nav_Spells -> {
                //Toast.makeText(this, "Spells clicked, still being developed", Toast.LENGTH_SHORT).show()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    SpellFragment()
                ).commit()
            }
            R.id.nav_Heroes -> {
                //Toast.makeText(this, "Heroes clicked, still being developed", Toast.LENGTH_SHORT).show()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    HeroesFragment()
                ).commit()
            }
            R.id.nav_aboutMe -> {
                Toast.makeText(this, "About Me clicked, you'll know more of me soon", Toast.LENGTH_SHORT).show()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if( drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}