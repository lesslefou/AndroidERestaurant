package fr.isen.duterte.androiderestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ItemViewActivity : AppCompatActivity() {
    lateinit var title : TextView
    lateinit var item: Item
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_view)

        title = findViewById(R.id.nameItem)
        item = intent.getSerializableExtra("item") as Item


        title.text = item.name_fr
    }
}