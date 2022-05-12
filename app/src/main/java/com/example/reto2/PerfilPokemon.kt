package com.example.reto2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.reto2.databinding.ActivityMainBinding
import com.example.reto2.databinding.ActivityPerfilPokemonBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import model.Pokemon
import model.PokemonAdd
import java.util.*

class PerfilPokemon : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilPokemonBinding
    lateinit var detailsListViewModel: DetailsListViewModel
    private lateinit var poke: Pokemon
    private lateinit var pokemonUser: PokemonAdd
    private lateinit var userID : String
    private lateinit var IDcaught : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_pokemon)

        binding= ActivityPerfilPokemonBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        var namePokemon = intent.extras?.getString("pokemon")
        userID = intent.extras?.getString("userID").toString()
        IDcaught = intent.extras?.getString("idAtrapado").toString()


        detailsListViewModel = ViewModelProvider(this).get(DetailsListViewModel::class.java)

        detailsListViewModel.GETListOfDetails(namePokemon!!)

        detailsListViewModel._DetailsList.observe(this){ pokemon ->

            binding.namePokemon.text = ""

            binding.details.text = ""

            binding.namePokemon.append("${pokemon.name}")

            poke = Pokemon(pokemon.uid,pokemon.name,pokemon.details,pokemon.abilities)
        }

        binding.remove.setOnClickListener{

            //LO DE ELIMINAR
            lifecycleScope.launch(Dispatchers.IO) {
               val db = Firebase.firestore.collection("users").document(userID)
                    .collection("pokemons").whereEqualTo("uuid",IDcaught)
                    .get().await().documents



            }

        }

        binding.atraparBtnPerfil.setOnClickListener {

            val collectionUsers = Firebase.firestore.collection("users")
            val firebase = Firebase.firestore.collection("Pokemons")

            //BUSCAMOS EN LA RAMA DE POKEMONS SI EL POKEN YA ESTA DENTRO DE NUESTRA BASE DE DATOS
            val query = firebase.whereEqualTo("name", poke.name)
            query.get()
                .addOnCompleteListener { documents ->
                    //SI EL POKEMON NO ESTA, LO AGREGAMOS
                    if (documents.result?.size() == 0) {
                        firebase.document(poke.uid).set(poke)
                        //LE AGREGAMOS AL USUARIO EL POKEMON ATRAPADO
                        collectionUsers.document(userID).collection("pokemons")
                            .document(UUID.randomUUID().toString()).set(poke)

                    } else {

                        //EL POKEMON SI ESTA EN LA RAMA
                        lateinit var uid: String
                        lateinit var name: String

                        for (document in documents.result!!) {
                            //ID DEL POKEMON QUE YA ESTA EN LA RAMA
                            uid = document.get("uid").toString()
                            name = document.get("name").toString()
                            //OBJETO SIMPLE PARA AGREGAR EL POKEMON AL USUARIO
                            pokemonUser = PokemonAdd(
                                Date().time,
                                uid,
                                name
                            )
                            //AGREGAR EL POKEMON AL USUARIO
                            collectionUsers.document(userID).collection("pokemons")
                                .document(UUID.randomUUID().toString()).set(pokemonUser)
                            break
                        }
                    }
                }
        }

    }


}