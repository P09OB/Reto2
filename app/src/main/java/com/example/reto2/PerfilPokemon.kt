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
    private lateinit var detailsListViewModel: DetailsListViewModel

    private lateinit var namePokemon: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_pokemon)

        /*val sharedPreference = getSharedPreferences("datos",Context.MODE_PRIVATE)
        namePokemon = sharedPreference.getString("user", "NO_FOUND").toString()*/

        binding= ActivityPerfilPokemonBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        //RECIBE EL ID DEL POKEMON PARA BUSCARLO EN FIREBASE
        var pokemonn= intent.extras?.getString("pokemon")

        Log.e("el pokemon es", pokemonn.toString())

        //BUSCA SUS DATOS
        val firebase = Firebase.firestore.collection("Pokemons")
        val query = firebase.whereEqualTo("uid", pokemonn)

        query.get()
            .addOnCompleteListener { documents ->
                for (document in documents.result!!) {
                    //Log.e("no se que hago", document.get("name").toString())
                    binding.namePokemon.setText(document.get("name").toString())
                }
            }

        /*Log.e("NOMBRE", ""+namePokemon)

        //binding
        binding= ActivityPerfilPokemonBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        detailsListViewModel = ViewModelProvider(this).get(DetailsListViewModel::class.java)

        detailsListViewModel.GETListOfDetails(namePokemon)

        detailsListViewModel._DetailsList.observe(this){ pokemon ->

            binding.namePokemon.text = ""

            binding.details.text = ""

                binding.namePokemon.append("${pokemon.name}")
        }*/
    }
}