package fr.isen.duterte.androiderestaurant.erestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import fr.isen.duterte.androiderestaurant.HomeActivity
import fr.isen.duterte.androiderestaurant.R
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

        val file = File(cacheDir.absolutePath + "panier.json")

        panierList = if(file.exists()) {
            Gson().fromJson( file.readText(), PanierList::class.java )
        } else {
            PanierList(arrayListOf())
        }


        monRecycler = binding.recycleViewPanier
        monRecycler.layoutManager = LinearLayoutManager(this)
        monRecycler.adapter = PanierAdapter(panierList.panier){
            deleteItem(it)
        }

        binding.btnCommander.setOnClickListener{
            commander()
        }

    }

    /**
     * Finalisation de la commande
     */
    private fun commander(){
        panierList = PanierList(arrayListOf())
        val strPanier = Gson().toJson(panierList, PanierList::class.java)
        File(cacheDir.absolutePath + "panier.json").writeText(strPanier )
        sharedPreferenceUpdate(0)

        val intent = Intent(this, HomeActivity::class.java)
        Toast.makeText(applicationContext, R.string.commanderToast, Toast.LENGTH_SHORT).show()
        startActivity(intent)

    }

    /**
     * Suppression de l'item du fichier et réduction de la quantité des shared preferences
     */
    private fun deleteItem(item: ItemPanier) {
        Log.d("panier", panierList.panier.size.toString() )
        if (panierList.panier.size == 0){
            panierList = PanierList(arrayListOf())
            sharedPreferenceUpdate(0)
        }
        else {
            panierList.panier.forEach {
                if (it.apiItems.equals(item)) {
                    panierList.panier.remove(it)
                    sharedPreferenceUpdate(-it.quantity)
                }
            }
        }

        val strPanier = Gson().toJson(panierList, PanierList::class.java)
        File(cacheDir.absolutePath + "panier.json").writeText(strPanier )
    }

    /**
     * Mise à jour des Shared Preferences sécurisé du panier
     */
    fun sharedPreferenceUpdate(quantity: Int) {
        val masterKeyAlias = MasterKeys.getOrCreate(
        MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "PANIER",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        var nbItem = sharedPreferences.getInt("nbItems", 0)
        if (quantity == 0){
            nbItem = 0
        } else {
            nbItem += quantity
        }
        sharedPreferences.edit()
            .putInt("nbItems",nbItem)
            .apply()
    }
}
