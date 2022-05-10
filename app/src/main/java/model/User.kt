package model

data class User(
    var uid: String = "",
    var username: String ="",
    var idPokemons: ArrayList<String>? = null
)