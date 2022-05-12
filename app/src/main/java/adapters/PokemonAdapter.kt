package adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2.DetailsListViewModel
import com.example.reto2.PerfilPokemon
import com.example.reto2.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import model.Pokemon
import model.PokemonAdd
import viewholders.PokemonVH
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PokemonAdapter : RecyclerView.Adapter<PokemonVH>() {

    private val pokemons = ArrayList<PokemonAdd>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pokemonrow, parent, false)
        return PokemonVH(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PokemonVH, position: Int) {
        val current = Date(pokemons[position].date)
        val formatter = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val date = formatter.format(current)

        holder.pokemonfecha.text= date

        val firebase = Firebase.firestore.collection("Pokemons")
        val query = firebase.whereEqualTo("uid", pokemons[position].uuidPokemon)

        query.get()
            .addOnCompleteListener { documents ->
                for (document in documents.result!!) {
                    holder.pokemonNombreRow.text=document.get("name").toString()
                }
            }

        holder.pokemonrow.setOnClickListener{
            goPerfilPokemon(it.context, position)
        }
    }

    override fun getItemCount(): Int {
        return pokemons.size
    }

    fun goPerfilPokemon(context: Context, position: Int){
        val intent = Intent(context, PerfilPokemon::class.java).apply {
            putExtra("pokemon", pokemons[position].name)
            putExtra("idAtrapado", pokemons[position].uuid)
        }
        context.startActivity(intent)

    }

    fun add(pokemon: PokemonAdd) {
        pokemons.add(pokemon)
        notifyItemInserted(pokemons.lastIndex)
    }

}