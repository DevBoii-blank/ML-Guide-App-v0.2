package com.example.mobilelegendsbuildguideapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilelegendsbuildguideapp.Common.Common
import com.example.mobilelegendsbuildguideapp.Model.Guide
import com.example.mobilelegendsbuildguideapp.Model.MyAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayout: LinearLayout

    //FIREBASE
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var users: DatabaseReference

    private lateinit var list: ArrayList<Guide>
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val HomeFragment: View = inflater.inflate(R.layout.fragment_home, container, false)

        linearLayout = HomeFragment.findViewById(R.id.MyGuidelayouts)
        recyclerView = HomeFragment.findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        //init firebase
        //auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
        users = db.getReference(Common.guide_tbl) //Guides

        //loadMyGuides()
        if(auth.currentUser != null) {
            linearLayout.visibility = LinearLayout.VISIBLE
            val uid = auth.currentUser!!.uid
            users.child(uid).addValueEventListener( object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    list = ArrayList<Guide>()
                    // Get Post object and use the values to update the UI
                    //for(dataSnapshot1: DataSnapshot in dataSnapshot.children) {
                        val post: Guide? = dataSnapshot.getValue(Guide::class.java)
                        if (post != null) {
                            list.add(post)
                            //Toast.makeText(context, "Got in! + HeroName: " + list[0].HeroName, Toast.LENGTH_SHORT).show()
                        }
                    //}
                    adapter = MyAdapter(activity, list)
                    recyclerView.adapter = adapter
                    //Toast.makeText(context, "Got in!", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(context, "Database Error, " + databaseError.message, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            linearLayout.visibility = LinearLayout.GONE
        }

        return HomeFragment
    }


}

