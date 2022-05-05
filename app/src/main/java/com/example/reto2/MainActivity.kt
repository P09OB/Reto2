package com.example.reto2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.reto2.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import model.User
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //binding
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)



        binding.loginBtn.setOnClickListener {
            val username = binding.loginNombreUser.text.toString()

            Firebase.firestore.collection("users")
                .document(username).get().addOnSuccessListener {
                    //El usuario ya esta
                    val user = it.toObject(User::class.java)
                    Log.e("Ya esta", user.toString())

                    startActivity(Intent(this, Pokedex::class.java))

                    //El usuario no esta, pero lo creamos
                    if (user == null) {
                        registerUserData()
                    }
                }
        }
    }


    fun registerUserData() {
        //uid
        val uid= Firebase.auth.currentUser?.uid
        uid?.let{}

        val user= User(
            UUID.randomUUID().toString(),
            binding.loginNombreUser.text.toString()
        )

        Firebase.firestore.collection("users").document(binding.loginNombreUser.text.toString()).set(user).addOnSuccessListener {
            Log.e("No estaba", "apenas creamos" +user)
        }

        startActivity(Intent(this, Pokedex::class.java))

    }
}