package model

import androidx.fragment.app.Fragment
import com.example.reto2.Pokedex
import java.util.*
import kotlin.collections.ArrayList

data class User(
    var uid: String = "",
    var username: String ="",
    var idPokemons: ArrayList<Pokedex.PokemonAdd>
)
