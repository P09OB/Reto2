package com.example.reto2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.reto2.databinding.ActivityPokedexBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Pokemon
import model.User
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.ArrayList

class Pokedex : AppCompatActivity() {

    private lateinit var binding: ActivityPokedexBinding

    private lateinit var userID: String
    private lateinit var pokemonUser: PokemonAdd

    private lateinit var adapter: ArrayAdapter<Pokemon>
    var idPokemonso: ArrayList<Fragment?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex)

        //OBTENEMOS EL ID DEL USUARIO
        val sharedPreference = getSharedPreferences("datos", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        userID = sharedPreference.getString("userID", "NO_FOUND").toString()

        //BINDING
        binding = ActivityPokedexBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //ADAPTER

        Firebase.firestore.collection("users")
            .whereEqualTo("uid",userID).get().addOnCompleteListener { task ->
                lateinit var existingUser : User

                for(document in task.result!!){
                    existingUser = document.toObject(User::class.java)

                    idPokemonso = existingUser.idPokemons

                }

                }

        //VER EL PERFIL DEL POKEMON
        binding.verBtn.setOnClickListener {

            startActivity(Intent(this, PerfilPokemon::class.java).apply {
                editor.putString("user", binding.atrapaPokemonText.text.toString())
                editor.commit()
            })
        }

        //ATRAPAR POKEMON
        binding.atraparBtn.setOnClickListener {

            //ENVIAMOS EL NOMBRE DEL POKEMON
            editor.putString("user", binding.atrapaPokemonText.text.toString())
            editor.commit()

            //FUNCION PARA OBTENER EL POKEMON DEL API - LE PASAMOS EL NOMBRE DEL POKEMON
            GETListOfDetails(binding.atrapaPokemonText.text.toString())

        }


    }

    fun GETListOfDetails(search: String) {
        lifecycleScope.launch(Dispatchers.IO) {

            //OBTENEMOS EL POKEMON DEL API
            val collectionUsers = Firebase.firestore.collection("users")
            val firebase = Firebase.firestore.collection("Pokemons")
            val url = URL("https://pokeapi.co/api/v2/pokemon/$search")
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            val json = connection.inputStream.bufferedReader().readText()

            //DESERIALIZACION  DEL POKEMON QUE OBTUVIMOS
            val pokemonObj = Gson().fromJson(json, DetailsListViewModel.PokemonObj::class.java)
            val details = ArrayList<DetailsListViewModel.Details>()
            val ability = ArrayList<DetailsListViewModel.Ability>()
            pokemonObj.abilities.forEach {
                ability.add(DetailsListViewModel.Ability(it.ability.name))
            }
            pokemonObj.stats.forEach {
                details.add(DetailsListViewModel.Details(it.stat, it.base_stat))
            }
            var pokemon = Pokemon(UUID.randomUUID().toString(), pokemonObj.name, details, ability)

            //CREAMOS UN OBJETO PARA GUARDAR LOS DATOS BASICOS DE POKEMON ATRAPADO
            pokemonUser = PokemonAdd(
                Date().time,
                pokemon.uid
            )

            //BUSCAMOS EN LA RAMA DE POKEMONS SI EL POKEN YA ESTA DENTRO DE NUESTRA BASE DE DATOS
            val query = firebase.whereEqualTo("name", pokemon.name)
            query.get()
                .addOnCompleteListener { documents ->
                    //SI EL POKEMON NO ESTA, LO AGREGAMOS
                    if (documents.result?.size() == 0) {
                        firebase.document(pokemonUser.uid).set(pokemon)
                        //LE AGREGAMOS AL USUARIO EL POKEMON ATRAPADO
                        collectionUsers.document(userID)
                            .update("idPokemons", FieldValue.arrayUnion(pokemonUser))
                            .addOnSuccessListener {
                                Log.e(">>>>>>>", "Se subio nuevo pokemon")
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    ">>>>>>>>>",
                                    "Error updating document",
                                    e
                                )
                            }

                    } else {

                        //EL POKEMON SI ESTA EN LA RAMA
                        lateinit var uid: String
                        for (document in documents.result!!) {
                            //ID DEL POKEMON QUE YA ESTA EN LA RAMA
                            uid = document.get("uid").toString()
                            //OBJETO SIMPLE PARA AGREGAR EL POKEMON AL USUARIO
                            pokemonUser = PokemonAdd(
                                Date().time,
                                uid
                            )
                            //AGREGAR EL POKEMON AL USUARIO
                            collectionUsers.document(userID)
                                .update("idPokemons", FieldValue.arrayUnion(pokemonUser))
                                .addOnSuccessListener {
                                    Log.e(">>>>>>>", "Se agrego uno nuevo") }
                                .addOnFailureListener { e ->
                                    Log.e(">>>>>>>>>", "Error updating document", e) }
                            break
                        }
                    }
                }
        }
}

data class PokemonAdd(

    var date: Long = 0,
    var uid: String
)

data class PokemonObj(
    var name: String,
    var stats: ArrayList<Details>,
    var abilities: ArrayList<Abilities>
)

data class Details(
    var stat: Stat,
    var base_stat: Int

)

data class Abilities(
    var ability: Ability
)

data class Ability(
    var name: String
)

data class Stat(
    var name: String
)
}

