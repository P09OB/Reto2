package com.example.reto2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.reto2.databinding.ActivityMainBinding
import com.example.reto2.databinding.ActivityPerfilPokemonBinding
import com.example.reto2.databinding.ActivityPokedexBinding

class Pokedex : AppCompatActivity() {

    private lateinit var binding: ActivityPokedexBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex)

        //binding
        binding= ActivityPokedexBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        binding.verBtn.setOnClickListener {
            startActivity(Intent(this, PerfilPokemon::class.java))
        }
    }
}