package com.example.reto2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.reto2.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import model.User
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var userActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //binding
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)


        binding.loginBtn.setOnClickListener {
            val username = binding.loginNombreUser.text.toString()
            val firebase = Firebase.firestore.collection("users")

            firebase.whereEqualTo("username",username)
                .get().addOnCompleteListener { documents ->

                    if(documents.result?.size() == 0){
                        registerUserData()
                    }

                    else{

                        lateinit var existingUser : User
                        for(document in documents.result!!){
                            existingUser = document.toObject(User::class.java)
                            val ids = existingUser.uid
                            goToPokedex(ids)
                            Log.e("HOLA", ""+ids)
                            break
                        }
                    }
            }

        }
    }

    fun registerUserData() {

       val user = User(
            UUID.randomUUID().toString(),
            binding.loginNombreUser.text.toString(),
            arrayListOf()
        )
        Firebase.firestore.collection("users").document(user.uid).set(user)
        goToPokedex(user.uid)

    }

    fun goToPokedex( Useruid : String){

        val sharedPreference = getSharedPreferences("datos", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        startActivity(Intent(this, Pokedex::class.java).apply {
            editor.putString("userID",Useruid)
            editor.commit()
        })

    }
}