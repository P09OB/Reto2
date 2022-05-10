package model

import com.example.reto2.DetailsListViewModel

data class Pokemon (
    var uid: String = "",
    var name: String = "",
    var details: ArrayList<DetailsListViewModel.Details>,
    var abilities: ArrayList<DetailsListViewModel.Ability>

)

