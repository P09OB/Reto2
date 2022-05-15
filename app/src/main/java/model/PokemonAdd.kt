package model

data class PokemonAdd(

    var date: Long = 0,
    var uuidPokemon: String = "",
    var name: String = "",
    var image: String? = null,
    var uuid: String = ""
)