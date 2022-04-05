package fr.isen.duterte.androiderestaurant.erestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        panierList = Gson().fromJson(File(cacheDir.absolutePath + "basket.json").readText(),
            PanierList::class.java)
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
        panierList.panier.forEach{
            panierList.panier.remove(it)
        }
        val strPanier = Gson().toJson(panierList, PanierList::class.java)
        File(cacheDir.absolutePath + "basket.json").writeText(strPanier )


        val intent = Intent(this, HomeActivity::class.java)
        Toast.makeText(applicationContext, R.string.commanderToast, Toast.LENGTH_SHORT).show()
        startActivity(intent)

    }

    /**
     * Suppression de l'item du fichier et réduction de la quantité des shared preferences
     */
    private fun deleteItem(item: ItemPanier) {
        panierList.panier.forEach {
            if (it.apiItems.equals(item)) {
                sharedPreferenceUpdate(-it.quantity)
                panierList.panier.remove(it)
            }
        }

        val strPanier = Gson().toJson(panierList, PanierList::class.java)
        File(cacheDir.absolutePath + "basket.json").writeText(strPanier )
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
        nbItem += quantity
        sharedPreferences.edit()
            .putInt("nbItems",nbItem)
            .apply()
    }
}
