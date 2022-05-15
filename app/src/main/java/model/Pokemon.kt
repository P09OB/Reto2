package model

import com.example.reto2.DetailsListViewModel

data class Pokemon (
    var uid: String = "",
    var name: String = "",
    var URL: String? = null,
    var details: ArrayList<DetailsListViewModel.Details>,
    var types: ArrayList<DetailsListViewModel.Type>

)

