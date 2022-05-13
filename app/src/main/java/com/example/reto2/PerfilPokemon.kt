package com.example.reto2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.reto2.databinding.ActivityPerfilPokemonBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Pokemon
import model.PokemonAdd
import java.util.*
import kotlin.collections.ArrayList

class PerfilPokemon : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilPokemonBinding
    lateinit var detailsListViewModel: DetailsListViewModel
    private lateinit var poke: Pokemon
    private lateinit var pokemonUser: PokemonAdd
    private lateinit var userID : String
    private lateinit var IDcaught : String
    private var pokeEliminado : Boolean= false
    private lateinit  var pokeNombre: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_pokemon)

        val sharedPreference = getSharedPreferences("datos", Context.MODE_PRIVATE)
        userID = sharedPreference.getString("userID", "NO_FOUND").toString()

        binding= ActivityPerfilPokemonBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        var namePokemon = intent.extras?.getString("pokemon")
        //userID = intent.extras?.getString("userID").toString()
        IDcaught = intent.extras?.getString("idAtrapado").toString()

        Log.e("idpoke",IDcaught)
        Log.e("iduser",userID)

        detailsListViewModel = ViewModelProvider(this).get(DetailsListViewModel::class.java)

        detailsListViewModel.GETListOfDetails(namePokemon!!)

        detailsListViewModel._DetailsList.observe(this){ pokemon ->
            pokeNombre= pokemon.name

            binding.namePokemon.text = ""

            binding.type.text = ""

            binding.details.text = ""

            binding.namePokemon.append("${pokemon.name}")

            val details : ArrayList<DetailsListViewModel.Details>
            val types : ArrayList<DetailsListViewModel.Type>

            details = pokemon.details
            types = pokemon.types

            for (deta in details){

                binding.details.append(deta.stat.name +" "+ deta.base_stat+'\n')
            }

            for(type in types){
                binding.type.append(type.name+'\n')
            }


            poke = Pokemon(pokemon.uid,pokemon.name,pokemon.details,pokemon.types)
        }


        //ELIMINAR AL POKEMON ATRAPADO
        binding.remove.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Firebase.firestore.collection("users")
                .document(userID)
                .collection("pokemons")
                .document(IDcaught).delete().addOnSuccessListener {
                    pokeEliminado = true
                    Log.e(">", "se elimino " + IDcaught)
                }.addOnFailureListener {
                    Log.e("error", it.message.toString())
                }

                withContext(Dispatchers.Main){
                    Toast.makeText(this@PerfilPokemon,"Se elimino a "+ pokeNombre ,Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@PerfilPokemon, Pokedex::class.java))
                }
            }
        }


        //ATRAPAR AL POKEMON
        binding.atraparBtnPerfil.setOnClickListener {
            val collectionUsers = Firebase.firestore.collection("users")
            val firebase = Firebase.firestore.collection("Pokemons")

            Log.e("ATRAPADO PERFIL", "entre")

            pokemonUser = PokemonAdd(
                Date().time,
                poke.uid,
                poke.name,
                UUID.randomUUID().toString()
            )

                //BUSCAMOS EN LA RAMA DE POKEMONS SI EL POKEN YA ESTA DENTRO DE NUESTRA BASE DE DATOS
                val query = firebase.whereEqualTo("name", poke.name)
                query.get()
                    .addOnCompleteListener { documents ->
                        //SI EL POKEMON NO ESTA, LO AGREGAMOS
                        if (documents.result?.size() == 0) {
                            firebase.document(poke.uid).set(poke)
                            //LE AGREGAMOS AL USUARIO EL POKEMON ATRAPADO
                            collectionUsers.document(userID).collection("pokemons")
                                .document(pokemonUser.uuid).set(pokemonUser)
                            Toast.makeText(this, "Se atrapo a ${pokemonUser.name}",Toast.LENGTH_LONG).show()

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
                                    name,
                                    UUID.randomUUID().toString()
                                )
                                //AGREGAR EL POKEMON AL USUARIO
                                collectionUsers.document(userID).collection("pokemons")
                                    .document(pokemonUser.uuid).set(pokemonUser)
                                Toast.makeText(this, "Se atrapo a ${pokemonUser.name}",Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, Pokedex::class.java))
                                finish()
                                break
                            }
                        }
                    }
        }

    }


}