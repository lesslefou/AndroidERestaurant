package fr.isen.duterte.androiderestaurant.erestaurant

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import fr.isen.duterte.androiderestaurant.R
import fr.isen.duterte.androiderestaurant.api.APIItems
import fr.isen.duterte.androiderestaurant.databinding.ActivityItemViewBinding
import java.io.File

class ItemViewActivity : AppCompatActivity() {
    private lateinit var item: APIItems
    private lateinit var binding: ActivityItemViewBinding
    private var quantity: Int = 1
    private lateinit var price: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        item = intent.getSerializableExtra("item") as APIItems

        //Nom
        binding.nameItem.text = item.name_fr

        //Description
        val ingredientsString = item.ingredients.joinToString(separator = ", ") { it.name_fr }
        binding.descriptionItem.text = ingredientsString

        //Quantité
        binding.quantity.text = quantity.toString()
        binding.btnMoins.setOnClickListener() { gestionQuantity('-') }
        binding.btnPlus.setOnClickListener() { gestionQuantity('+') }

        //Bouton prix ajouter
        displayPrice()

        //ViewPager Image
        var imgs = ArrayList<String>(item.images)
        var adapter = ViewPagerAdapter(imgs, this)
        binding.viewPager.adapter = adapter

        //AlertMessage
        binding.totalButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(R.string.alertMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.valider, DialogInterface.OnClickListener { dialog, id ->
                    ajoutPanier()
                })
                .setNegativeButton(R.string.annuler, DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
            val alert = dialogBuilder.create()
            alert.setTitle("Panier")
            alert.show()
        }

    }

    /**
     * Mise à jour du fichier panier.json avec la liste des éléments du panier
     */
    private fun ajoutPanier() {
        sharedPreferenceUpdate(this.quantity)

        val panierList = checkIfExist()
        val strPanier = Gson().toJson(panierList, PanierList::class.java)
        File(cacheDir.absolutePath + "panier.json").writeText(strPanier )
        finish()
    }


    /**
     * On vient checker si l'item à ajouter et ou non déjà dans le panier pour faire une mise à jour de la quantité ou l'insertion dans la liste
     */
    private fun checkIfExist(): PanierList? {
        var exist = false
        val panierList = Gson().fromJson(File(cacheDir.absolutePath + "panier.json").readText(),
            PanierList::class.java)

        panierList.panier.forEach{
            Log.d("Panier", "name ${it.apiItems.name_fr} , item ${this.item.name_fr}")
            if (it.apiItems.name_fr.equals(this.item.name_fr)) {
                exist = true
                it.quantity += this.quantity
            }
        }

        if (!exist) {
            panierList.panier.add(ItemPanier(this.item, this.quantity))
        }
        return panierList
    }

    /**
     * Récupération de la quantité choisie par l'utilisateur
     */
    private fun gestionQuantity(symbole: Char) {
        if (symbole.equals('-') && quantity > 1) {
            binding.quantity.text = (quantity - 1).toString()
            this.quantity -= 1
        } else if (symbole.equals('+')) {
            binding.quantity.text = (quantity + 1).toString()
            this.quantity += 1
        }
        displayPrice()
    }

    /**
     * Affichage personnalisé du prix de l'article
     */
    private fun displayPrice() {
        price = "Total " + (item.prices[0].price.toFloat() * this.quantity).toString() + "€"
        binding.totalButton.text = price
    }

    /**
     * Récupération des Shared Preferences sécurisé du panier + affichage
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "PANIER",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val sharedNbItems = sharedPreferences.getInt("nbItems", 0)

        var nb = menu.findItem(R.id.nbItems).actionView
        var nbText = nb.findViewById<TextView>(R.id.nbItemsText)
        nbText.text = sharedNbItems.toString()
        return true
    }

    /**
     * Mise à jour des Shared Preferences sécurisé du panier
     */
    private fun sharedPreferenceUpdate(quantity: Int) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
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

    /**
     * Redirection vers le panier au clique sur l'icon panier
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cardIcon -> {
                displayCard()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayCard() {
        val intent = Intent(this, PanierActivity::class.java)
        startActivity(intent)
    }

}