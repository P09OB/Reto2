package com.example.reto2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.reto2.databinding.ActivityMainBinding
import com.example.reto2.databinding.ActivityPerfilPokemonBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PerfilPokemon : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilPokemonBinding
    lateinit var detailsListViewModel: DetailsListViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_pokemon)

        binding= ActivityPerfilPokemonBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        var namePokemon = intent.extras?.getString("pokemon")

        detailsListViewModel = ViewModelProvider(this).get(DetailsListViewModel::class.java)

        detailsListViewModel.GETListOfDetails(namePokemon!!)

        detailsListViewModel._DetailsList.observe(this){ pokemon ->

            binding.namePokemon.text = ""

            binding.details.text = ""

            binding.namePokemon.append("${pokemon.name}")
        }

    }

    fun onResultFirebase(){

        //RECIBE EL ID DEL POKEMON PARA BUSCARLO EN FIREBASE


        //BUSCA SUS DATOS
        /*val firebase = Firebase.firestore.collection("Pokemons")
        val query = firebase.whereEqualTo("uid", pokemonn)

        query.get()
            .addOnCompleteListener { documents ->
                for (document in documents.result!!) {
                    //Log.e("no se que hago", document.get("name").toString())
                    binding.namePokemon.setText(document.get("name").toString())

                    var i= document.get("details")

                    binding.defensaText.setText(i.toString())
                    Log.e("masmasda",i.toString() )
                }
            }*/

    }


    fun onResultApi (namePokemon : String){



    }
}