package fr.isen.duterte.androiderestaurant.api

import java.io.Serializable

data class APIPrices(val id:String, val id_pizza:String, val id_size:String, val price:String, val create_date:String, val update_date:String, val size:String) :
    Serializable