package com.example.reto2

import adapters.PokemonAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reto2.databinding.ActivityPokedexBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Pokemon
import model.PokemonAdd
import model.User
import viewholders.PokemonVH
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.ArrayList


class Pokedex : AppCompatActivity() {

    private val binding: ActivityPokedexBinding by lazy {
        ActivityPokedexBinding.inflate(layoutInflater)
    }

    private lateinit var userID: String
    private lateinit var pokemonUser: PokemonAdd

    private val adapter by lazy {
        PokemonAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //OBTENEMOS EL ID DEL USUARIO
        val sharedPreference = getSharedPreferences("datos", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        userID = sharedPreference.getString("userID", "NO_FOUND").toString()

        //BINDING
        //binding = ActivityPokedexBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.listaPokemones.adapter = adapter
        binding.listaPokemones.layoutManager = LinearLayoutManager(this)
        binding.listaPokemones.setHasFixedSize(true)

        //ADAPTER
        lifecycleScope.launch(Dispatchers.IO) {
            val pokemons = Firebase.firestore.collection("users").document(userID).collection("pokemons")
                .get()
                .await().documents
            Log.e(">>>>>",""+pokemons)
            withContext(Dispatchers.Main){
                for(poke in pokemons){
                    val obj = poke.toObject(PokemonAdd::class.java)!!
                    adapter.add(obj)
                    Log.e(">>>>>",""+obj)
                }
            }

        }

        //VER EL PERFIL DEL POKEMON
        binding.verBtn.setOnClickListener {

            //BUSCA AL POKEMON POR SU NOMBRE, PARA ENVIAR EL ID AL PERFIL
            val firebase = Firebase.firestore.collection("Pokemons")
            val query = firebase.whereEqualTo("name", binding.atrapaPokemonText.text.toString())

            query.get()
                .addOnCompleteListener { documents ->
                    for (document in documents.result!!) {
                        val intent = Intent(this, PerfilPokemon::class.java).apply {
                            putExtra("pokemon", document.get("uid").toString())

                            /*editor.putString("user", binding.atrapaPokemonText.text.toString())
                            editor.commit()*/
                        }

                        startActivity(intent)
                    }
                }
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
                        collectionUsers.document(userID).collection("pokemons")
                            .document(UUID.randomUUID().toString()).set(pokemonUser)

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
                            collectionUsers.document(userID).collection("pokemons")
                                .document(UUID.randomUUID().toString()).set(pokemonUser)
                            break
                        }
                    }
                }
        }
}


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


