package viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2.databinding.PokemonrowBinding

class PokemonVH (itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding: PokemonrowBinding by lazy {
        PokemonrowBinding.bind(itemView)
    }

    val pokemonrow= binding.rowPokemon
    val pokemonNombreRow= binding.nombrePokemonRow
    val pokemonfecha= binding.fechaRow
    val pokemonImg= binding.imgPokemonRow
}