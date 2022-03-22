package fr.isen.duterte.androiderestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.duterte.androiderestaurant.databinding.ActivityPanierBinding
import java.io.File

class PanierActivity : AppCompatActivity()  {
    private lateinit var binding : ActivityPanierBinding
    private lateinit var panierList: ArrayList<ItemPanier>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPanierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        panierList = arrayListOf()

        val file = File(cacheDir.absolutePath + "basket.json")
        file.forEachLine {
            Log.d("Panier :", it)
        }

        binding.recycleViewPanier.layoutManager = LinearLayoutManager(this)
        binding.recycleViewPanier.adapter = PanierAdapter(panierList){
                //Permettre de supprimer
            }


            //Log.d("Item", File(cacheDir.absolutePath + "basket.json").readText())
    }

}