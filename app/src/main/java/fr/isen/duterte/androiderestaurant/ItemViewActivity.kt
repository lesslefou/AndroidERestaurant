package fr.isen.duterte.androiderestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.TextView
import fr.isen.duterte.androiderestaurant.databinding.ActivityItemViewBinding

class ItemViewActivity : AppCompatActivity() {
    private lateinit var item: APIItems
    private lateinit var binding: ActivityItemViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        item = intent.getSerializableExtra("item") as APIItems
        binding.nameItem.text = item.name_fr
        binding.descriptionItem.text = item.name_fr



        var imgs = ArrayList<String>(item.images)
        var adapter = ViewPagerAdapter(imgs,this)
        binding.viewPager.adapter = adapter

    }
}