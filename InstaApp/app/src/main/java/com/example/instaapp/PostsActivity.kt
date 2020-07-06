package com.example.instaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instaapp.models.Post
import com.example.instaapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_posts.*


private  var  signedInUser:User?=null

private  const val TAG="PostsActivity"
private const val EXTRA_USERNAME="EXTRA_USERNAME"
open class PostsActivity : AppCompatActivity() {

     private  lateinit var firestoreDb:FirebaseFirestore
    private lateinit var  posts:MutableList<Post>
    private lateinit var adapter:PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        //create the data source
        posts= mutableListOf()
        //create the adapter
        adapter= PostsAdapter(this,posts)
        //Bind the adapter and layout manager to the RV
        rvPosts.adapter=adapter
        rvPosts.layoutManager=LinearLayoutManager(this)

        firestoreDb = FirebaseFirestore.getInstance()

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot->
                signedInUser=userSnapshot.toObject(User::class.java)
                Log.i(TAG,"signed in user: $signedInUser")
            }
            .addOnFailureListener {exception ->
                Log.i(TAG,"Failure fetching signed in user",exception)

            }


        var postsReference = firestoreDb.collection("posts")
            .limit(20)
            .orderBy("creation_time_ms",Query.Direction.DESCENDING)
        //single user
           val username=intent.getStringExtra(EXTRA_USERNAME)
        if(username!=null){
            supportActionBar?.title=username
            postsReference=postsReference.whereEqualTo("user.username",username)

        }



        postsReference.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null || querySnapshot == null) {
                Log.e(TAG, "Exception when queryin posts", firebaseFirestoreException)
                return@addSnapshotListener
            }
                    val postList=querySnapshot.toObjects(Post::class.java)
                    posts.clear()
                  posts.addAll(postList)
             adapter.notifyDataSetChanged()
                    for (post in postList) {
                        Log.i(TAG,"Post ${post}")


                    }




        }


        floatingActionButton2.setOnClickListener {
          val intent=Intent(this,CreateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_profile){
            val intent=Intent(this,ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }




}




