package com.example.reto2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.reto2.databinding.ActivityPokedexBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import model.Pokemon
import model.User
import java.util.*
import kotlin.collections.ArrayList

class Pokedex : AppCompatActivity() {

    private lateinit var binding: ActivityPokedexBinding
    private lateinit var detailsListViewModel: DetailsListViewModel
    private var pokemonData: Boolean = false

    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex)

        val sharedPreference = getSharedPreferences("datos", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        userID = sharedPreference.getString("userID", "NO_FOUND").toString()

        //binding
        binding = ActivityPokedexBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        binding.verBtn.setOnClickListener {
            //editor.putString("name",binding.atrapaPokemonText.text.toString())
            startActivity(Intent(this, PerfilPokemon::class.java).apply {
                //putExtra("name",binding.atrapaPokemonText.text.toString())
                editor.putString("user", binding.atrapaPokemonText.text.toString())
                editor.commit()
            })


        }

        binding.atraparBtn.setOnClickListener {


            editor.putString("user", binding.atrapaPokemonText.text.toString())
            editor.commit()

            detailsListViewModel = ViewModelProvider(this).get(DetailsListViewModel::class.java)

            detailsListViewModel.GETListOfDetails(binding.atrapaPokemonText.text.toString())

            detailsListViewModel._DetailsList.observe(this) {

                it.forEach { pokemon ->

                    val firebase = Firebase.firestore.collection("Pokemons")
                    val collectionUsers = Firebase.firestore.collection("users")

                    val pokemonObj = PokemonAdd (
                        Date().time,
                        pokemon.uid
                    )

                    val list = ArrayList<PokemonAdd>()

                    //OBTENER SUS POKEMONS
                    collectionUsers.document(userID)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    val getDoc = document.toObject(User::class.java)!!
                                    Log.e(">>>>>",""+getDoc.idPokemons)
                                    val liste = getDoc.idPokemons.toArray()
                                }
                            }
                        }


                    //Verifica
                   val query = firebase.whereEqualTo("name", pokemon.name)
                    query.get()
                        .addOnCompleteListener { documents ->

                            if (documents.result?.size() == 0) {
                                //POKEMON NUEVO
                                firebase.document(pokemon.uid).set(pokemon)
                                //LE AGREGA AL USUARIO EL POKEMON
                                collectionUsers.document(userID)
                                    .update("idPokemons", list)
                                    .addOnSuccessListener {
                                        Log.e(
                                            ">>>>>>>", "DocumentSnapshot successfully updated!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            ">>>>>>>>>",
                                            "Error updating document",
                                            e
                                        )
                                    }

                            } else {
                                //POKEMON VIEJO

                                for (document in documents.result!!) {
                                    var uid = document.getString("uid")



                                    collectionUsers.document(userID)
                                        .update("idPokemons", list )
                                        .addOnSuccessListener {
                                            Log.e(
                                                ">>>>>>>",
                                                "DocumentSnapshot successfully updated!"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                ">>>>>>>>>",
                                                "Error updating document",
                                                e
                                            )
                                        }
                                    break
                                }


                            }

                        }


                }


            }

        }


    }

    data class  PokemonAdd(

        var date : Long = 0,
        var uid: String
    )
}

