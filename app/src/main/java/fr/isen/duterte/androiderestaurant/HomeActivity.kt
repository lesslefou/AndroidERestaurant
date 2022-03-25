package fr.isen.duterte.androiderestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import fr.isen.duterte.androiderestaurant.databinding.ActivityMainBinding

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
    }

    private fun goToCategory(s: String) {
        val intent = Intent(this,CategoryActivity::class.java)
        val toast = Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT)
        toast.show()
        intent.putExtra("categorie",s)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("HomeActivity", "onDestroy !")
    }

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