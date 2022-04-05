package fr.isen.duterte.androiderestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import fr.isen.duterte.androiderestaurant.ble.BleLauncherActivity
import fr.isen.duterte.androiderestaurant.databinding.ActivityMainBinding
import fr.isen.duterte.androiderestaurant.erestaurant.CategoryActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferenceCreation()

        binding.entreesText.setOnClickListener{goToCategory(getString(R.string.entrees))}
        binding.platsText.setOnClickListener{goToCategory(getString(R.string.plats))}
        binding.desertsText.setOnClickListener{goToCategory(getString(R.string.desserts))}
        binding.blebtn.setOnClickListener{
            val intent = Intent(this, BleLauncherActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Redirection vers la page de la catégorie choisie
     */
    private fun goToCategory(s: String) {
        val intent = Intent(this, CategoryActivity::class.java)
        val toast = Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT)
        toast.show()
        intent.putExtra("categorie",s)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("HomeActivity", "onDestroy !")
    }

    /**
     * Mise en place de Shared Preferences sécurisé pour le nombre d'items dans le panier
     */
    private fun sharedPreferenceCreation() {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "PANIER",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        sharedPreferences.edit()
            .putInt("nbItems",0)
            .apply()
    }
}