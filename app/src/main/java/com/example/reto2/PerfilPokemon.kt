package com.example.reto2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.reto2.databinding.ActivityMainBinding
import com.example.reto2.databinding.ActivityPerfilPokemonBinding

class PerfilPokemon : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilPokemonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_pokemon)

        //binding
        binding= ActivityPerfilPokemonBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)


    }
}