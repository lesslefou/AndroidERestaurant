package fr.isen.duterte.androiderestaurant.erestaurant

import fr.isen.duterte.androiderestaurant.api.APIItems
import java.io.Serializable

class ItemPanier (var apiItems: APIItems, var quantity:Int) : Serializable