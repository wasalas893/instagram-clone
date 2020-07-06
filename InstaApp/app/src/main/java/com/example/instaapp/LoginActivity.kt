package com.example.instaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val auth=FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            goPostsActivity()
        }

        btnLogin.setOnClickListener {
            btnLogin.isEnabled=false
            val email=etEmail.text.toString()
            val password=etPassword.text.toString()

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Email/Password cannot be empty",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //firebase athentication check

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                btnLogin.isEnabled=true
                if(task.isSuccessful){
                    Toast.makeText(this,"Success!",Toast.LENGTH_SHORT).show()
                    goPostsActivity()
                }else{

                    Toast.makeText(this,"Authentication failed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goPostsActivity() {
       val intent=Intent(this,PostsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
