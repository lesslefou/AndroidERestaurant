package fr.isen.duterte.androiderestaurant

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import fr.isen.duterte.androiderestaurant.databinding.ActivityPanierBinding
import java.io.File

class PanierActivity : AppCompatActivity() {
    private lateinit var monRecycler: RecyclerView
    private lateinit var binding : ActivityPanierBinding
    private lateinit var panierList: PanierList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPanierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        panierList = Gson().fromJson(File(cacheDir.absolutePath + "basket.json").readText(),PanierList::class.java)
        monRecycler = binding.recycleViewPanier
        monRecycler.layoutManager = LinearLayoutManager(this)
        monRecycler.adapter = PanierAdapter(panierList.panier){
            deleteItem(it)
        }

    }

    private fun deleteItem(item: ItemPanier) {
        panierList.panier.forEach {
            if (it.apiItems.equals(item)) {
                //ItemViewActivity.sharedPreferenceUpdate(-it.quantity)
                sharedPreferenceUpdate(-it.quantity)
                panierList.panier.remove(it)
            }
        }


        val strPanier = Gson().toJson(panierList, PanierList::class.java)
        File(cacheDir.absolutePath + "basket.json").writeText(strPanier )
    }

    fun sharedPreferenceUpdate(quantity: Int) {
        val sharedPreference =  getSharedPreferences("PANIER", Context.MODE_PRIVATE)
        var nbItem = 0
        sharedPreference.getInt("nbItems", nbItem)
        nbItem += quantity
        var editor = sharedPreference.edit()
        editor.putInt("nbItems",nbItem)
        editor.apply()
    }
}
