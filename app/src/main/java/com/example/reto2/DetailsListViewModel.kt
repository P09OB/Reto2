package com.example.reto2

import android.accounts.AbstractAccountAuthenticator
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Pokemon
import java.net.URL
import java.util.*
import java.util.Collections.fill
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.ArrayList


class DetailsListViewModel : ViewModel() {

    var _DetailsList: MutableLiveData<Pokemon> = MutableLiveData()


    fun GETListOfDetails(search: String) {
        viewModelScope.launch(Dispatchers.IO){

            val url = URL("https://pokeapi.co/api/v2/pokemon/$search")
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            val json = connection.inputStream.bufferedReader().readText()

            val pokemonObj = Gson().fromJson(json,PokemonObj::class.java)

            val details = ArrayList<Details>()
            val ability = ArrayList<Ability>()
            var pokemon : Pokemon?


            pokemonObj.abilities.forEach {
                ability.add(Ability(it.ability.name))
            }

            pokemonObj.stats.forEach {

                details.add(Details( it.stat,it.base_stat))

            }

            pokemon = Pokemon(UUID.randomUUID().toString(),pokemonObj.name,details, ability)

            withContext(Dispatchers.Main){
                _DetailsList.value = pokemon!!
                pokemon = null

            }

        }

    }

    data class PokemonObj(
        var name : String,
        var stats : ArrayList<Details>,
        var abilities : ArrayList<Abilities>
    )

    data class Details (
        var stat : Stat,
        var base_stat : Int

    )

    data class Abilities (
        var ability : Ability
    )

    data class Ability(
        var name: String
    )

    data class Stat (
        var name : String
    )

    }