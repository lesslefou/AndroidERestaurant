package fr.isen.duterte.androiderestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import fr.isen.duterte.androiderestaurant.databinding.ActivityMainBinding

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.entreesText.setOnClickListener(this)
        binding.platsText.setOnClickListener(this)
        binding.desertsText.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val intent = Intent(this,CategoryActivity::class.java)
        when(view?.id){
            R.id.entreesText->{
                toastMessage(view)
                intent.putExtra("categorie",getString(R.string.entrees))
            }
            R.id.platsText->{
                toastMessage(view)
                intent.putExtra("categorie",getString(R.string.plats))
            }
            R.id.desertsText->{
                toastMessage(view)
                intent.putExtra("categorie",getString(R.string.desserts))
            }
        }
        startActivity(intent)
    }

    private fun toastMessage (view: View?) {
        when(view?.id) {
            R.id.entreesText -> {
                val toast = Toast.makeText(applicationContext, R.string.entrees, Toast.LENGTH_SHORT)
                toast.show()
            }
            R.id.platsText -> {
                val toast = Toast.makeText(applicationContext, R.string.plats, Toast.LENGTH_SHORT)
                toast.show()
            }
            R.id.desertsText -> {
                val toast = Toast.makeText(applicationContext, R.string.desserts, Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("HomeActivity", "onDestroy !")
    }

}