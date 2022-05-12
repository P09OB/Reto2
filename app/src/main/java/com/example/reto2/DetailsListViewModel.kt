package com.example.reto2

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
            val types = ArrayList<Type>()
            var pokemon : Pokemon?


            pokemonObj.types.forEach {
                types.add(Type(it.type.name))
            }

            pokemonObj.stats.forEach {

                details.add(Details( it.stat,it.base_stat))

            }

            pokemon = Pokemon(UUID.randomUUID().toString(),pokemonObj.name,details, types)

            withContext(Dispatchers.Main){
                _DetailsList.value = pokemon!!
            }

        }

    }

    data class PokemonObj(
        var name : String,
        var stats : ArrayList<Details>,
        var types : ArrayList<Types>
    )

    data class Details (
        var stat : Stat,
        var base_stat : Int

    )

    data class Types (
        var type : Type
    )

    data class Type(
        var name: String
    )

    data class Stat (
        var name : String
    )

    }